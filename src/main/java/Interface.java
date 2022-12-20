import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Interface extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Depot");
        BorderPane borderPane = new BorderPane();
        Pane pane = new Pane();
        pane.setScaleY(-1);
        borderPane.setTop(addHBox());
        borderPane.setLeft(addVBox());
        borderPane.setRight(addVBox());
        borderPane.setBottom(addHBox());
        borderPane.setCenter(pane);
        Scene scene = new Scene(borderPane,800,400);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        //String filename = "instances/1t/TerminalA_20_10_3_2_100.json";
        String filename = "instances/3t/TerminalA_20_10_3_2_160.json";
        //String filename = "instances/5t/TerminalB_20_10_3_2_160.json";
        //String filename = "instances/6t/Terminal_10_10_3_1_100.json";
        //String filename = "instances/7t/TerminalC_10_10_3_2_80.json";
        //String filename = "instances/8t/TerminalC_10_10_3_2_80.json";
        //String filename = "instances/9t/TerminalC_10_10_3_2_100.json";
        //String filename = "instances/10t/TerminalC_10_10_3_2_100.json";
        //String filename = "instances/2mh/MH2Terminal_20_10_3_2_100.json";
        //String filename = "instances/4mh/MH2Terminal_20_10_3_2_160.json";

        Input input = new Input(filename);

        Terminal startTerminal = input.getTerminal();
        int tlength = startTerminal.length;
        int twidth = startTerminal.width;
        List<List<Slot>> area = startTerminal.area;
        List<Slot> slots = startTerminal.slots;
        List<Container> containers = startTerminal.containers;

        //plot containers
        List<Integer> plotted = new ArrayList<>();
        for (int h = 0; h < startTerminal.maxHeight; h++) {
            for (Slot slot : slots) {
                if (slot.containers.size() > h) {
                    int id = slot.containers.get(h).id;
                    if (!plotted.contains(id)) {
                        plotted.add(id);
                        Rectangle rectangle = newContainer(pane, tlength, twidth, slot.y, slot.x, id, slot.containers.get(h).length, 10100-h);
                        pane.getChildren().add(rectangle);
                    }
                }
            }
        }

        //plot height
        for (Slot slot : startTerminal.slots) {
            Text text = new Text(String.valueOf(slot.containers.size()));
            text.setId("h" + slot.id);
            text.setScaleY(-1);
            text.setFont(Font.font(16));
            text.translateXProperty().set(pane.getWidth()/tlength*(slot.x + 0.5));
            text.translateYProperty().set(pane.getHeight()/twidth*(slot.y + 0.5));
            pane.getChildren().add(text);
        }

        //plot kranen
        for(Crane c: startTerminal.cranes){
            Rectangle crane = new Rectangle();
            crane.heightProperty().bind(pane.heightProperty().divide(twidth).divide(2));
            crane.widthProperty().bind(pane.widthProperty().divide(tlength).divide(2));
            crane.translateXProperty().set(pane.getWidth()/tlength * c.x - crane.getWidth()/2);
            crane.translateYProperty().set(pane.getHeight()/twidth * c.y - crane.getHeight()/2);
            crane.setId("c" + c.id);
            crane.setViewOrder(0);

            Rectangle frame = new Rectangle();
            frame.heightProperty().bind(pane.heightProperty());
            frame.widthProperty().bind(pane.widthProperty().divide(tlength).divide(4));
            frame.translateXProperty().bind(crane.translateXProperty().add(crane.widthProperty().divide(4)));
            frame.setViewOrder(0);

            pane.getChildren().add(frame);
            pane.getChildren().add(crane);
        }

        //voer acties uit
        List<Action> actions = Main.main(filename);
        System.out.println("actionsize " + actions.size());
        List<List<OutputRecord>> records =  ActionToOutput.toOutput(actions, startTerminal.cranes);
        SequentialTransition sequence = new SequentialTransition();
        int prior = 10000;
        double time = 0;

        for(List<OutputRecord> batch : records){
            Timeline pickup = new Timeline();
            Timeline move = new Timeline();
            double duration = 0;

            for(OutputRecord record : batch){
                //safety distance related?
                if(record.cid == -1){
                    Rectangle crane = (Rectangle) pane.lookup("#c" + record.craneid);
                    KeyValue safetyx = new KeyValue(crane.translateXProperty(), (pane.getWidth()/tlength)*record.eposx - crane.getWidth()/2);
                    KeyFrame safetyframe = new KeyFrame(Duration.seconds(1),safetyx);
                    pickup.getKeyFrames().add(safetyframe);
                    continue;
                }

                //actual move
                Rectangle rectangle = (Rectangle) pane.lookup("#" + record.action.container.id);
                Rectangle crane = (Rectangle) pane.lookup("#c" + record.craneid);
                List<Text> textsource = new ArrayList<>();
                List<Text> textdestination = new ArrayList<>();

                for (int i = 0; i < record.action.container.length; i++) {
                    textsource.add((Text) pane.lookup("#h" + (record.action.prevSlot.id + i)));
                    textdestination.add((Text) pane.lookup("#h" + (record.action.slot.id + i)));
                }

                //movement kraan naar container
                KeyValue pickupx = new KeyValue(crane.translateXProperty(), (pane.getWidth()/tlength)*record.pposx - crane.getWidth()/2);
                KeyValue pickupy = new KeyValue(crane.translateYProperty(), (pane.getHeight()/twidth)*record.pposy - crane.getHeight()/2);
                int finalPrior = prior;
                KeyFrame pickupframe = new KeyFrame(Duration.seconds(record.ptime - time), actionEvent -> {
                    rectangle.toFront();
                    rectangle.setViewOrder(finalPrior);
                    for (Text text : textsource) {
                        text.setText(String.valueOf(Integer.parseInt(text.getText()) - 1));
                    }
                }, pickupx, pickupy);
                prior--;
                pickup.getKeyFrames().add(pickupframe);


                //movement container
                KeyValue keyValuex = new KeyValue(rectangle.translateXProperty(), (pane.getWidth()/tlength)*record.action.slot.x);
                KeyValue keyValuey = new KeyValue(rectangle.translateYProperty(), (pane.getHeight()/twidth)*record.action.slot.y);
                KeyValue keyValuecranex = new KeyValue(crane.translateXProperty(), (pane.getWidth()/tlength)*record.eposx - crane.getWidth()/2);
                KeyValue keyValuecraney = new KeyValue(crane.translateYProperty(), (pane.getHeight()/twidth)*record.eposy - crane.getHeight()/2);

                KeyFrame moveframe = new KeyFrame(Duration.seconds(record.etime-record.ptime), actionEvent -> {
                    for (Text text : textdestination) {
                        text.setText(String.valueOf(Integer.parseInt(text.getText()) + 1));
                    }
                }, keyValuex, keyValuey, keyValuecranex, keyValuecraney);
                move.getKeyFrames().add(moveframe);

                //set duration
                if(record.etime - time > duration){
                    duration = record.etime - time;
                }
            }

            sequence.getChildren().add(pickup);
            sequence.getChildren().add(move);

            time += duration;
        }

        sequence.play();

//        for (Action action : actions) {
//            String id = "#" + action.container.id;
//            Rectangle rectangle = (Rectangle) pane.lookup(id);
//            List<Text> textsource = new ArrayList<>();
//            List<Text> textdestination = new ArrayList<>();
//            for (int i = 0; i < action.container.length; i++) {
//                textsource.add((Text) pane.lookup("#h" + (action.prevSlot.id + i)));
//                textdestination.add((Text) pane.lookup("#h" + (action.slot.id + i)));
//            }
//
//            KeyValue keyValueposx = new KeyValue(crane.translateXProperty(), rectangle.getTranslateX() + (rectangle.getWidth()/2));
//            KeyValue keyValueposy = new KeyValue(crane.translateYProperty(), rectangle.getTranslateY() + (rectangle.getHeight()/2));
//
//            int finalPrior = prior;
//            KeyFrame keyFramepos = new KeyFrame(Duration.seconds(2), actionEvent -> {
//                rectangle.toFront();
//                rectangle.setViewOrder(finalPrior);
//                for (Text text : textsource) {
//                    text.setText(String.valueOf(Integer.parseInt(text.getText()) - 1));
//                }
//            }, keyValueposx, keyValueposy);
//            prior--;
//
//            Timeline timeline1 = new Timeline(keyFramepos);
//            sequence.getChildren().add(timeline1);
//
//            KeyValue keyValuex = new KeyValue(rectangle.translateXProperty(), pane.getWidth()/tlength*action.slot.x);
//            KeyValue keyValuey = new KeyValue(rectangle.translateYProperty(), pane.getHeight()/twidth*action.slot.y);
//            KeyValue keyValuecranex = new KeyValue(crane.translateXProperty(), (pane.getWidth()/tlength)*(action.slot.x + 0.5)-crane.getWidth()/2);
//            KeyValue keyValuecraney = new KeyValue(crane.translateYProperty(), (pane.getHeight()/twidth)*(action.slot.y + 0.5)-crane.getHeight()/2);
//
//            KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), actionEvent -> {
//                for (Text text : textdestination) {
//                    text.setText(String.valueOf(Integer.parseInt(text.getText()) + 1));
//                }
//            }, keyValuex, keyValuey, keyValuecranex, keyValuecraney);
//            Timeline timeline2 = new Timeline(keyFrame);
//            sequence.getChildren().add(timeline2);
//            rectangle.toFront();
//            crane.toFront();
//            frame.toFront();
//        }
//        sequence.play();
    }

    public Rectangle newContainer(Pane pane, int tlength, int twidth, int i, int j, int index, int length, int prior) {
        Random random = new Random();
        Rectangle rectangle = new Rectangle(200,100);
        rectangle.translateXProperty().set(pane.getWidth()/tlength*j);
        rectangle.translateYProperty().set(pane.getHeight()/twidth*i);
        rectangle.widthProperty().bind(pane.widthProperty().divide(tlength).multiply(length));
        rectangle.heightProperty().bind(pane.heightProperty().divide(twidth));
        rectangle.setFill(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        rectangle.setId(String.valueOf(index));
        rectangle.setViewOrder(prior);
        return rectangle;
    }

    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(50, 50, 50, 50));
        hbox.setSpacing(10);

        return hbox;
    }

    public VBox addVBox() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(50, 50, 50, 50));
        vbox.setSpacing(10);

        return vbox;
    }

}
