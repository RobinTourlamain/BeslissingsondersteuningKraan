import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Interface extends Application {

    public static void main(String[] args) {
        launch(args);
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

        Input input = new Input("instances/5t/TerminalB_20_10_3_2_160.json");
        //Input input = new Input("instances/2mh/MH2Terminal_20_10_3_2_100.json");
        Terminal startTerminal = input.getTerminal();
        int tlength = startTerminal.length;
        int twidth = startTerminal.width;
        List<List<Slot>> area = startTerminal.area;
        List<Slot> slots = startTerminal.slots;

        //plot containers
        List<Integer> plotted = new ArrayList<>();
        for(int h = 0; h < startTerminal.maxHeight; h++){
            for(Slot slot : slots){
                if(slot.containers.size() > h){
                    int id = slot.containers.get(h).id;
                    if(!plotted.contains(id)){
                        plotted.add(id);
                        pane.getChildren().add(newContainer(pane, tlength, twidth, slot.y, slot.x, id, slot.containers.get(h).length));
                    }
                }
            }
        }

        //plot kranen
        Rectangle crane = new Rectangle();
        crane.heightProperty().bind(pane.heightProperty().divide(twidth).divide(2));
        crane.widthProperty().bind(pane.widthProperty().divide(tlength).divide(2));

        Rectangle frame = new Rectangle();
        frame.heightProperty().bind(pane.heightProperty());
        frame.widthProperty().bind(pane.widthProperty().divide(tlength).divide(4));
        frame.translateXProperty().bind(crane.translateXProperty().add(crane.widthProperty().divide(4)));

        pane.getChildren().add(frame);
        pane.getChildren().add(crane);

        //voer acties uit
        List<Action> actions = Main.main();
        System.out.println("actionsize " + actions.size());
        SequentialTransition sequence = new SequentialTransition();
        for(Action action : actions){
            String id = "#" + action.container.id;
            Rectangle rectangle = (Rectangle) pane.lookup(id);
            rectangle.toFront();
            crane.toFront();
            frame.toFront();

            KeyValue keyValueposx = new KeyValue(crane.translateXProperty(), rectangle.getTranslateX() + (rectangle.getWidth()/2));
            KeyValue keyValueposy = new KeyValue(crane.translateYProperty(), rectangle.getTranslateY() + (rectangle.getHeight()/2));

            KeyFrame keyFramepos = new KeyFrame(Duration.seconds(4), keyValueposx, keyValueposy);
            Timeline timeline1 = new Timeline(keyFramepos);
            sequence.getChildren().add(timeline1);

            KeyValue keyValuex = new KeyValue(rectangle.translateXProperty(), pane.getWidth()/tlength*action.slot.x);
            KeyValue keyValuey = new KeyValue(rectangle.translateYProperty(), pane.getHeight()/twidth*action.slot.y);
            KeyValue keyValuecranex = new KeyValue(crane.translateXProperty(), (pane.getWidth()/tlength)*(action.slot.x + 0.5)-crane.getWidth()/2);
            KeyValue keyValuecraney = new KeyValue(crane.translateYProperty(), (pane.getHeight()/twidth)*(action.slot.y + 0.5)-crane.getHeight()/2);

            KeyFrame keyFrame = new KeyFrame(Duration.seconds(4),keyValuex,keyValuey,keyValuecranex,keyValuecraney);
            Timeline timeline2 = new Timeline(keyFrame);
            sequence.getChildren().add(timeline2);
        }
        sequence.play();
    }

    public Rectangle newContainer(Pane pane, int tlength, int twidth, int i, int j, int index, int length){
        Random random = new Random();
        Rectangle rectangle = new Rectangle(200,100);
        rectangle.translateXProperty().set(pane.getWidth()/tlength*j);
        rectangle.translateYProperty().set(pane.getHeight()/twidth*i);
        rectangle.widthProperty().bind(pane.widthProperty().divide(tlength).multiply(length));
        rectangle.heightProperty().bind(pane.heightProperty().divide(twidth));
        rectangle.setFill(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        rectangle.setId(String.valueOf(index));
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
