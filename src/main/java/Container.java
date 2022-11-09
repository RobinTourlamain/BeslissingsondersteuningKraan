import java.util.ArrayList;
import java.util.List;

public class Container {
    int id;
    int length;
    List<Slot> slots;

    public Container(int id, int length) {
        this.id = id;
        this.length = length;
        this.slots = new ArrayList<>();
    }

    public void assignSlot(Slot slot) {
        this.slots.add(slot);
        slot.containers.add(this);
    }
}
