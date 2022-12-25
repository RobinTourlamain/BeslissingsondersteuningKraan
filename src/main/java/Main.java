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

import java.util.*;

public class Main extends Application {

    public static String pathToOutput = null;
    public static String pathToInput1 = null;
    public static String pathToInput2 = null;


    public static void main(String[] args) {

        if (args.length != 0) {
            pathToOutput = args[0];
            pathToInput1 = args[1];
            if (args.length > 2) {
                pathToInput2 = args[2];
            }
        }

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

        //String filename1 = "instances/1t/TerminalA_20_10_3_2_100.json";
        //String filename1 = "instances/3t/TerminalA_20_10_3_2_160.json";
        //String filename1 = "instances/5t/TerminalB_20_10_3_2_160.json";
        //String filename1 = "instances/6t/Terminal_10_10_3_1_100.json";
        //String filename1 = "instances/7t/TerminalC_10_10_3_2_80.json";
        //String filename1 = "instances/8t/TerminalC_10_10_3_2_80.json";
        //String filename1 = "instances/9t/TerminalC_10_10_3_2_100.json";
        //String filename1 = "instances/10t/TerminalC_10_10_3_2_100.json";
        //String filename1 = "instances/2mh/MH2Terminal_20_10_3_2_100.json";
        String filename1 = "instances/4mh/MH2Terminal_20_10_3_2_160.json";

        String[] filenameSplit = filename1.split("/");
        String filename2 = filenameSplit[0] + "/" + filenameSplit[1] + "/" + "target" + filenameSplit[2];

        if (pathToInput1 != null) {
            filename1 = pathToInput1;
        }
        if (pathToInput2 != null) {
            filename2 = pathToInput2;
        }


        Input input = new Input(filename1);

        Terminal startTerminal = input.getTerminal();
        int terminalLength = startTerminal.length;
        int terminalWidth = startTerminal.width;
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
                        Rectangle rectangle = newContainer(pane, terminalLength, terminalWidth, slot.y, slot.x, id, slot.containers.get(h).length, 10100-h);
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
            text.translateXProperty().set(pane.getWidth() / terminalLength * (slot.x + 0.5));
            text.translateYProperty().set(pane.getHeight() / terminalWidth * (slot.y + 0.5));
            pane.getChildren().add(text);
        }

        //plot kranen
        for (Crane c: startTerminal.cranes) {
            Rectangle crane = new Rectangle();
            crane.heightProperty().bind(pane.heightProperty().divide(terminalWidth).divide(2));
            crane.widthProperty().bind(pane.widthProperty().divide(terminalLength).divide(2));
            crane.translateXProperty().set((pane.getWidth() / terminalLength * c.x) - (crane.getWidth() / 2));
            crane.translateYProperty().set((pane.getHeight() / terminalWidth * c.y) - (crane.getHeight() / 2));
            crane.setId("c" + c.id);
            crane.setViewOrder(0);

            Rectangle frame = new Rectangle();
            frame.heightProperty().bind(pane.heightProperty());
            frame.widthProperty().bind(pane.widthProperty().divide(terminalLength).divide(4));
            frame.translateXProperty().bind(crane.translateXProperty().add(crane.widthProperty().divide(4)));
            frame.setViewOrder(0);

            pane.getChildren().add(frame);
            pane.getChildren().add(crane);
        }

        //voer acties uit
        List<Action> actions = Prepare.prepare(filename1, filename2);
        System.out.println("actionsize " + actions.size());
        List<List<OutputRecord>> recordMatrix =  ActionToOutput.toOutput(actions, startTerminal.cranes, pathToOutput);
        SequentialTransition sequence = new SequentialTransition();
        int prior = 10000;
        double time = 0;

        for (List<OutputRecord> recordList : recordMatrix) {
            Timeline pickup = new Timeline();
            Timeline move = new Timeline();
            double duration = 0;

            for (OutputRecord record : recordList) {
                //safety distance related?
                if (record.containerId == -1) {
                    Rectangle crane = (Rectangle) pane.lookup("#c" + record.craneId);
                    KeyValue safetyX = new KeyValue(crane.translateXProperty(), (pane.getWidth() / terminalLength * record.endPosX) - (crane.getWidth() / 2));
                    KeyFrame safetyFrame = new KeyFrame(Duration.seconds(record.endTime - time), safetyX);
                    pickup.getKeyFrames().add(safetyFrame);
                    continue;
                }

                //actual move
                Rectangle rectangle = (Rectangle) pane.lookup("#" + record.action.container.id);
                Rectangle crane = (Rectangle) pane.lookup("#c" + record.craneId);
                List<Text> textSource = new ArrayList<>();
                List<Text> textDestination = new ArrayList<>();

                for (int i = 0; i < record.action.container.length; i++) {
                    textSource.add((Text) pane.lookup("#h" + (record.action.prevSlot.id + i)));
                    textDestination.add((Text) pane.lookup("#h" + (record.action.slot.id + i)));
                }

                //movement kraan naar container
                KeyValue pickupX = new KeyValue(crane.translateXProperty(), (pane.getWidth()/terminalLength*record.pickupPosX) - (crane.getWidth() / 2));
                KeyValue pickupY = new KeyValue(crane.translateYProperty(), (pane.getHeight()/terminalWidth*record.pickupPosY) - (crane.getHeight() / 2));
                int finalPrior = prior;
                KeyFrame pickupFrame = new KeyFrame(Duration.seconds(record.pickupTime - time), actionEvent -> {
                    rectangle.toFront();
                    rectangle.setViewOrder(finalPrior);
                    for (Text text : textSource) {
                        text.setText(String.valueOf(Integer.parseInt(text.getText()) - 1));
                    }
                }, pickupX, pickupY);
                prior--;
                pickup.getKeyFrames().add(pickupFrame);


                //movement container
                KeyValue keyValueX = new KeyValue(rectangle.translateXProperty(), (pane.getWidth() / terminalLength) * record.action.slot.x);
                KeyValue keyValueY = new KeyValue(rectangle.translateYProperty(), (pane.getHeight() / terminalWidth) * record.action.slot.y);
                KeyValue keyValueCraneX = new KeyValue(crane.translateXProperty(), ((pane.getWidth() / terminalLength) * record.endPosX) - (crane.getWidth() / 2));
                KeyValue keyValueCraneY = new KeyValue(crane.translateYProperty(), ((pane.getHeight() / terminalWidth) * record.endPosY) - (crane.getHeight() / 2));

                KeyFrame moveFrame = new KeyFrame(Duration.seconds(record.endTime - record.pickupTime), actionEvent -> {
                    for (Text text : textDestination) {
                        text.setText(String.valueOf(Integer.parseInt(text.getText()) + 1));
                    }
                }, keyValueX, keyValueY, keyValueCraneX, keyValueCraneY);
                move.getKeyFrames().add(moveFrame);

                //set duration
                if(record.endTime - time > duration){
                    duration = record.endTime - time;
                }
            }

            sequence.getChildren().add(pickup);
            sequence.getChildren().add(move);

            time += duration;
        }

        sequence.play();
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
