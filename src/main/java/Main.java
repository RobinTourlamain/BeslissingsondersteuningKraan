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

        List<List<Slot>> area = makeArea(slots);


        Crane crane1 = new Crane(area, 1,1);
        Crane crane2 = new Crane(area,1,1);
        crane1.setLocation(area.get(0).get(0));
        crane2.setLocation(area.get(area.size()-1).get(0));
        List<Crane> cranes = new ArrayList<>();
        cranes.add(crane1);
        //cranes.add(crane2);                                          //veroorzaakt deadlock want staat in de weg
        Depot depot = new Depot(area, cranes,1);

        depot.addMove(4,4,crane1);

        depot.calcMovements();

        crane1.printPath();



    }

    public static List<List<Slot>> makeArea(List<Slot> slots) {
        List<List<Slot>> area = new ArrayList<>(new ArrayList<>());

        for (Slot slot : slots) {
            area.get(slot.x).add(slot.y, slot);
        }

        return area;
    }
}
