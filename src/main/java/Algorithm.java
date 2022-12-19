import java.util.*;

public class Algorithm {

    public static List<Container> findContainersAboveMaxHeight(Terminal terminal) {
        Set<Container> containersToMove = new HashSet<>();

        for (Slot slot : terminal.slots) {
            if (slot.containers.size() > terminal.targetHeight) {
                containersToMove.add(slot.containers.peek());
            }
        }

        return new ArrayList<>(containersToMove);
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

    public static Slot findTransferSlot(Terminal terminal, Container container) {
        assert terminal.cranes.size() == 2 : "Wrong crane count";

        List<Crane> craneCopy = new ArrayList<>(terminal.cranes);
        craneCopy.sort(Comparator.comparing(crane -> crane.xMin));

        int xMin = craneCopy.get(1).xMin;
        int xMax = craneCopy.get(0).xMax;

        if (container.length > 1) {
            xMin -= 1;
            xMax += 1;
        }

        for (int y = 0; y < terminal.width; y++) {
            for (int x = xMin; x + container.length < xMax; x++) {
                if (Algorithm.containerFits(terminal, new ArrayList<>(), container, x, y)) {
                    return terminal.area.get(x).get(y);
                }
            }
        }

        return null;
    }

    public static boolean staysWithinOneCraneArea(Terminal terminal, Container container, Slot currentSlot) {
        boolean oneCrane = false;

        for (Crane crane : terminal.cranes) {
            int xMin = crane.xMin;
            int xMax = crane.xMax;

            if (container.length > 1) {
                if (xMin == 0) {
                    xMax += 1;
                }
                if (xMax == terminal.length) {
                    xMin -= 1;
                }
            }

            Slot containerSlot = container.slots.get(0);
            if (xMin <= containerSlot.x && containerSlot.x <= xMax && xMin <= currentSlot.x && currentSlot.x <= xMax) {
                oneCrane = true;
                break;
            }
        }

        return oneCrane;
    }
}
