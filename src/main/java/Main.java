import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, JsonException {

        FileReader fileReader = new FileReader("terminal_4_3.json");
        JsonObject deserialize = (JsonObject) Jsoner.deserialize(fileReader);
        List<Container> containers= new ArrayList<>(Input.readContainers(deserialize));
        List<Slot> slots = new ArrayList<>(Input.readSlots(deserialize));
        Input.assign(slots, containers, deserialize);

        containers.forEach(c-> c.slots.forEach(s -> System.out.println(s.id)));

        new GUI();
//        Coordinaat[][][] c = new Coordinaat[5][5][5];
//        for (int i = 0; i < 5; i++) {
//            for (int j = 0; j < 5; j++) {
//                for (int k = 0; k < 5; k++) {
//                    c[i][j][k] = new Coordinaat(i, j, k);
//                }
//            }
//        }
//        Crane crane1 = new Crane(c, 1,1);
//        Crane crane2 = new Crane(c,1,1);
//        crane1.setLocation(new Coordinaat(0,0,0));
//        crane2.setLocation(new Coordinaat(2,2,0));
//        List<Crane> cranes = new ArrayList<>();
//        cranes.add(crane1);
//        //cranes.add(crane2);                                          //veroorzaakt deadlock want staat in de weg
//        Depot depot = new Depot(5,5,5, cranes,1);
//
//        depot.addMove(4,4,crane1);
//
//        depot.calcMovements();
//
//        crane1.printPath();

    }
}
