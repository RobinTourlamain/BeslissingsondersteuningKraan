import java.util.ArrayList;
import java.util.HashSet;
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

    public boolean checkPlaceable(List<Slot> destSlots) {
        int height = destSlots.get(0).containers.size();
        for (Slot slot : destSlots) {
            //check if containers in the slots are stacked to the same height
            if (slot.containers.size() != height) {
                return false;
            }
            //Check if containers below are larger
            if (slot.containers.peek().length > length) {
                return false;
            }
            //containers below are smaller, but do they extend beyond where we want to place the container
            if (!new HashSet<>(destSlots).containsAll(slot.containers.peek().slots)) {
                return false;
            }
        }
        return true;
    }

    public Coordinate getAttachPoint() {
        float middleX = 0;
        float middleY = 0;

        for (Slot slot : slots) {
            middleX += slot.x;
            middleY += slot.y;
        }

        middleX /= length;
        middleY /= length;

        return new Coordinate(middleX, middleY);
    }
}
