package com.example.beslissingsondersteuningkraan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Container {
    public int id;
    public int length;
    public List<Slot> slots;

    public Container(int id, int length) {
        this.id = id;
        this.length = length;
        this.slots = new ArrayList<>();
    }

    public void removeFromSlots() {
        for (Slot slot : slots) {
            slot.containers.remove(this);
        }
        this.slots.clear();
    }

    public void assignSlot(Slot slot) {
        this.slots.add(slot);
        slot.addContainer(this);
    }

    public boolean isMovable() {
        for (Slot slot : slots) {
            if (slot.containers.peek() != this) {
                return false;
            }
        }
        return true;
    }

    public boolean isPlaceable(List<Slot> destSlots) {
        int height = destSlots.get(0).containers.size();
        for (Slot slot : destSlots) {
            //check if containers in the slots are stacked to the same height
            if (slot.containers.size() != height) {
                return false;
            }
            //check if max height is exceeded if you add the container
            if (slot.containers.size() + 1 > slot.maxHeight) {          //veranderd van >= naar >
                return false;
            }
            //Check if containers below
            if (slot.containers.size() > 0) {
                //Check if containers below are larger
                if (slot.containers.peek().length > length) {
                    return false;
                }
                //containers below are smaller, but do they extend beyond where we want to place the container
                if (!new HashSet<>(destSlots).containsAll(slot.containers.peek().slots)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPlaceableTargetHeight(List<Slot> destSlots, int targetHeight) {
        int height = destSlots.get(0).containers.size();
        for (Slot slot : destSlots) {
            //check if containers in the slots are stacked to the same height
            if (slot.containers.size() != height) {
                return false;
            }
            //check if max height is exceeded if you add the container
            if (slot.containers.size() + 1 > targetHeight) {          //veranderd van >= naar >
                return false;
            }
            //Check if containers below
            if (slot.containers.size() > 0) {
                //Check if containers below are larger
                if (slot.containers.peek().length > length) {
                    return false;
                }
                //containers below are smaller, but do they extend beyond where we want to place the container
                if (!new HashSet<>(destSlots).containsAll(slot.containers.peek().slots)) {
                    return false;
                }
            }
        }
        return true;
    }
}
