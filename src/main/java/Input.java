import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Input {
    private final Terminal terminal;

    public Input(String fileName) {
        FileReader fileReader;
        JsonObject jsonObject;
        try {
            fileReader = new FileReader(fileName);
            jsonObject = (JsonObject) Jsoner.deserialize(fileReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String terminalName = (String) jsonObject.get("name");
        int terminalLength = ((BigDecimal) jsonObject.get("length")).intValue();
        int terminalWidth = ((BigDecimal) jsonObject.get("width")).intValue();
        int terminalMaxHeight = ((BigDecimal) jsonObject.get("maxheight")).intValue();

        int terminalTargetHeight = 0;
        if (jsonObject.containsKey("targetheight")) {
            terminalTargetHeight = ((BigDecimal) jsonObject.get("targetheight")).intValue();
            System.out.println(terminalTargetHeight);
        }

        List<Container> containers = readContainers(jsonObject);
        List<Slot> slots = readSlots(jsonObject, terminalMaxHeight);
        slots.sort(Comparator.comparing(slot -> slot.id));
        assignContainersToSlots(slots, containers, jsonObject);
        
        List<List<Slot>> area = makeArea(slots);
        List<Crane> cranes = readCranes(jsonObject);

        this.terminal = new Terminal(terminalName, terminalLength, terminalWidth, terminalMaxHeight, terminalTargetHeight, containers, slots, area, cranes);

    }

    public Terminal getTerminal() {
        return terminal;
    }

    public static List<Container> readContainers(JsonObject jsonObject){
        List<Container> containers = new ArrayList<>();
        JsonArray jsContainers = (JsonArray) jsonObject.get("containers");
        for(Object jsContainer : jsContainers){
            JsonObject container = (JsonObject) jsContainer;
            containers.add(new Container(((BigDecimal) container.get("id")).intValue(), ((BigDecimal) container.get("length")).intValue()));
        }
        return containers;
    }

    public static List<Slot> readSlots(JsonObject jsonObject, int maxHeight) {
        List<Slot> slots = new ArrayList<>();
        JsonArray jsSlots = (JsonArray) jsonObject.get("slots");
        for (Object jsSlot : jsSlots) {
            JsonObject slot = (JsonObject) jsSlot;
            slots.add(new Slot(((BigDecimal) slot.get("id")).intValue(), ((BigDecimal) slot.get("x")).intValue(), ((BigDecimal) slot.get("y")).intValue(), maxHeight));
        }
        return slots;
    }

    public static void assignContainersToSlots(List<Slot> slots, List<Container> containers, JsonObject jsonObject) {
        JsonArray jsAssignments = (JsonArray) jsonObject.get("assignments");
        for (Object jsAssignment : jsAssignments) {
            JsonObject assignment = (JsonObject) jsAssignment;
            Container container = containers.get(((BigDecimal) assignment.get("container_id")).intValue());
            int slotId = ((BigDecimal) assignment.get("slot_id")).intValue();

            for (int i = 0; i < container.length; i++) {
                Slot slot = slots.get(slotId + i);
                container.assignSlot(slot);
            }
        }
    }

    public static List<List<Slot>> makeArea(List<Slot> slots) {
        List<List<Slot>> area = new ArrayList<>();

        List<Slot> slotsCopy = new ArrayList<>(slots);

        slotsCopy.sort(Comparator.comparing((Slot slot) -> slot.x).thenComparing((Slot slot) -> slot.y));

        for (Slot slot : slotsCopy) {
            if (area.size() < slot.x + 1) {
                area.add(new ArrayList<>());
            }
            area.get(slot.x).add(slot.y, slot);
        }

        return area;
    }

    public static List<Crane> readCranes(JsonObject jsonObject) {
        List<Crane> cranes = new ArrayList<>();
        JsonArray jsCranes = (JsonArray) jsonObject.get("cranes");
        for (Object jsCrane : jsCranes) {
            JsonObject crane = (JsonObject) jsCrane;
            cranes.add(new Crane(((BigDecimal) crane.get("id")).intValue(), ((BigDecimal) crane.get("x")).intValue(), ((BigDecimal) crane.get("y")).intValue(), ((BigDecimal) crane.get("xmin")).intValue(), ((BigDecimal) crane.get("xmax")).intValue(), ((BigDecimal) crane.get("ymin")).intValue(), ((BigDecimal) crane.get("ymax")).intValue(), ((BigDecimal) crane.get("xspeed")).intValue(), ((BigDecimal) crane.get("yspeed")).intValue()));
        }
        return cranes;
    }
}
