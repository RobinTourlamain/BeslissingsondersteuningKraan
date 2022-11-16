import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Input {

    private final List<Container> containers;
    private final List<Slot> slots;
    private final List<List<Slot>> area;

    public Input(String fileName) {
        FileReader fileReader;
        JsonObject deserialize;
        try {
            fileReader = new FileReader(fileName);
            deserialize = (JsonObject) Jsoner.deserialize(fileReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.containers = new ArrayList<>(Input.readContainers(deserialize));
        this.slots = new ArrayList<>(Input.readSlots(deserialize));

        assignContainersToSlots(slots, containers, deserialize);

        this.area = makeArea(slots);
    }

    public List<Container> getContainers() {
        return containers;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public List<List<Slot>> getArea() {
        return area;
    }

    public static List<Container> readContainers(JsonObject js){
        List<Container> containers = new ArrayList<>();
        JsonArray jsContainers = (JsonArray) js.get("containers");
        for(Object jsContainer : jsContainers){
            JsonObject container = (JsonObject) jsContainer;
            containers.add(new Container(((BigDecimal) container.get("id")).intValue(), ((BigDecimal) container.get("length")).intValue()));
        }
        return containers;
    }

    public static List<Slot> readSlots(JsonObject jsonObject) {
        List<Slot> slots = new ArrayList<>();
        JsonArray jsSlots = (JsonArray) jsonObject.get("slots");
        for (Object jsSlot : jsSlots) {
            JsonObject slot = (JsonObject) jsSlot;
            slots.add(new Slot(((BigDecimal) slot.get("id")).intValue(), ((BigDecimal) slot.get("x")).intValue(), ((BigDecimal) slot.get("y")).intValue()));
        }
        return slots;
    }

    public static void assignContainersToSlots(List<Slot> slots, List<Container> containers, JsonObject jsonObject) {
        JsonArray jsAssignments = (JsonArray) jsonObject.get("assignments");
        for (Object jsAssignment : jsAssignments) {
            JsonObject assignment = (JsonObject) jsAssignment;
            Container container = containers.get(((BigDecimal) assignment.get("container_id")).intValue() - 1);
            JsonArray jsSlots = (JsonArray) assignment.get("slot_id");
            List<Integer> slotIds = new ArrayList<>();
            for (Object jsSlot : jsSlots) {
                slotIds.add(((BigDecimal) jsSlot).intValue());
            }
            for (int slotId : slotIds) {
                Slot slot = slots.get(slotId - 1);
                container.assignSlot(slot);
            }
        }
    }

    public static List<List<Slot>> makeArea(List<Slot> slots) {
        List<List<Slot>> area = new ArrayList<>();

        slots.sort(Comparator.comparing(slot -> slot.x));

        for (Slot slot : slots) {
            if (area.size() < slot.x + 1) {
                area.add(new ArrayList<>());
            }
            area.get(slot.x).add(slot.y, slot);
        }

        return area;
    }
}
