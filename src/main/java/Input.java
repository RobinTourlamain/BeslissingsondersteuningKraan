import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Input {

    public static List<Container> readContainers(JsonObject js){
        List<Container> containers = new ArrayList<>();
        JsonArray jscontainers = (JsonArray) js.get("containers");
        for(Object obj : jscontainers){
            JsonObject container = (JsonObject) obj;
            containers.add(new Container(((BigDecimal) container.get("id")).intValue(), ((BigDecimal) container.get("length")).intValue()));
        }
        return containers;
    }

    public static List<Slot> readSlots(JsonObject jsonObject) {
        List<Slot> slots = new ArrayList<>();
        JsonArray jsSlots = (JsonArray) jsonObject.get("slots");
        for (Object obj : jsSlots) {
            JsonObject slot = (JsonObject) obj;
            slots.add(new Slot(((BigDecimal) slot.get("id")).intValue(), ((BigDecimal) slot.get("x")).intValue(), ((BigDecimal) slot.get("y")).intValue()));
        }
        return slots;
    }

    public static void assign(List<Slot> slots, List<Container> containers, JsonObject jsonObject) {
        JsonArray jsAssign = (JsonArray) jsonObject.get("assignments");
        for (Object obj : jsAssign) {
            JsonObject assignment = (JsonObject) obj;
            Container container = containers.get(((BigDecimal) assignment.get("container_id")).intValue()-1);
            JsonArray jsslots = (JsonArray) assignment.get("slot_id");
            List<Integer> slot_ids = new ArrayList<>();
            for (Object jsslot : jsslots) {
                slot_ids.add(((BigDecimal) jsslot).intValue());
            }
            for (int obj2 : slot_ids) {
                Slot slot = slots.get(obj2-1);
                container.assignSlot(slot);
            }
        }
    }
}
