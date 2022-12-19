import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Algorithm {

    public static List<Container> findExposed(List<List<Slot>> area) {
        List<Container> res = new ArrayList<>();

        for (List<Slot> slotList : area) {
            for (Slot slot : slotList) {
                if (slot.containers.isEmpty()) {
                    continue;
                }
                Container topContainer = slot.containers.peek();
                if (res.contains(topContainer)) {
                    continue;
                }
                if (topContainer.isMovable()) {
                    res.add(topContainer);
                }
            }
        }

        return res;
    }

    public static List<Integer> findChangedContainerIds(Terminal terminal, Terminal targetTerminal) {

        List<Integer> movedContainerIds = new ArrayList<>();

        for (int i = 0; i < terminal.containers.size(); i++) {
            if (terminal.containers.get(i).slots.get(0).id != targetTerminal.containers.get(i).slots.get(0).id) {
                movedContainerIds.add(terminal.containers.get(i).id);
            }
        }

        return movedContainerIds;
    }

    public static List<Container> findContainersAboveMaxHeight(Terminal terminal) {
        Set<Container> containersToMove = new HashSet<>();

        for (Slot slot : terminal.slots) {
            if (slot.containers.size() > terminal.targetHeight) {
                containersToMove.add(slot.containers.peek());
            }
        }

        return new ArrayList<>(containersToMove);
    }

    ///////////////////////////////////////////////////TO TARGET TERMINAL ALGORTITHM//////////////////////////////////

    public static void clearThisSlotAndLengthOfContainer(Terminal startTerminal, int slotnumber, int height, Container container) {
        //maak lengte vrij
        clearLength(startTerminal, slotnumber, height, container.length, container.slots);
    }

    //clear de spot waar de container moet komen
    public static void clearLength(Terminal startTerminal, int slotnumber, int height, int containerlength, List<Slot> blacklist) {
        List<Slot> blacklistcopy = new ArrayList<>(blacklist);
        //voeg alle slots in lengte toe aan blacklist
        for (int i = 0; i < containerlength; i++) {
            blacklistcopy.add(startTerminal.slots.get(slotnumber + i));
        }
        //clear alle slots in lengte
        for (int i = 0; i < containerlength; i++) {
            clearSlot(startTerminal, slotnumber+i, height, blacklistcopy);
        }
    }

    //make a spot void of containers
    public static void clearSlot(Terminal startTerminal, int slotnumber, int height, List<Slot> blacklist) {
        Slot slot = startTerminal.slots.get(slotnumber);
        List<Slot> blacklistcopy = new ArrayList<>(blacklist);

        while (slot.containers.size() > height) {
            //er staat nog een container op deze hoogte
            moveContainerOutOfWay(startTerminal, blacklistcopy, slot.containers.peek());
        }
    }

    public static void moveContainerOutOfWay(Terminal startTerminal, List<Slot> blacklist, Container container) {
        //if not movable clear all spots above container first
        if (!container.isMovable()) {
            List<Slot> blacklistcopy = new ArrayList<>(blacklist);
            blacklistcopy.addAll(container.slots);

            //clear slots boven container die je wilt moven
            for (Slot slot : container.slots) {
                clearSlot(startTerminal, slot.id, slot.containers.indexOf(container)+1,blacklistcopy);
            }
        }
        System.out.println("ik wil " + container.id + " moven");
        //move but not to blacklisted spots
        for (int y = 0; y < startTerminal.width; y++) {
            for (int x = 0; x + container.length <= startTerminal.length; x++) {
                if (containerFits(startTerminal, blacklist, container, x, y)) {
                    System.out.println("Move: " + container.id + " from slot " + container.slots.get(0) + " to slot " + startTerminal.area.get(x).get(y));
                    container.removeFromSlots();
                    for (int i = 0; i < container.length; i++) {
                        container.assignSlot(startTerminal.area.get(x+i).get(y));
                        //TODO toevoegen aan een movelijst
                    }
                    return;
                }
            }
        }
    }

    public static boolean containerFits(Terminal startTerminal, List<Slot> blacklist, Container container, int x, int y) {
        //check of placable
        List<Slot> placehere = new ArrayList<>();
        for (int i = 0; i < container.length; i++) {
            placehere.add(startTerminal.area.get(x+i).get(y));
        }
        if (!container.isPlaceable(placehere)) {
            return false;
        }
        //check of blacklisted
        for (int i = 0; i < container.length; i++) {
            if (blacklist.contains(startTerminal.area.get(x+i).get(y))) {
                return false;
            }
        }
        return true;
    }

    public static void prepareContainerMove(Terminal startTerminal, Container container) {
        if (!container.isMovable()) {
            //clear slots boven container die je wilt moven
            for (Slot slot : container.slots) {
                clearSlot(startTerminal, slot.id, slot.containers.indexOf(container)+1,container.slots);
            }
        }
    }

    public static void moveContainerToTarget(Terminal startTerminal, Container container, List<Slot> destination) {
        System.out.println("Move: " + container.id + " from slot " + container.slots.get(0).id + " to slot " + destination.get(0).id);
        container.removeFromSlots();
        for (int i = 0; i < container.length; i++) {
            destination.forEach(container::assignSlot);
            //TODO toevoegen aan een movelijst
        }
    }
}
