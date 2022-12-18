import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Recursion {

    public static List<Action> makeSolution(Terminal terminal, Slot currentSlot, int height, Container container) {
        List<Action> result = new ArrayList<>();

        recursion(result, terminal, currentSlot, height, container);

        return result;
    }

    public static boolean recursion(List<Action> result, Terminal terminal, Slot currentSlot, int height, Container container) {
        boolean containerPlaceable = true;
        for (int i = 0; i < container.length; i++) {
            Slot slot = terminal.slots.get(currentSlot.id + i);
            if (slot.containers.size() > height) {
                containerPlaceable = false;
                break;
            }
        }
        //containerPlaceable = Algorithm.containerFits(terminal, new ArrayList<>(), container, currentSlot.x, currentSlot.y);

        if (!container.isMovable() || !containerPlaceable) {
            System.out.println("recursie");
            //TODO: when should it return false?
            List<Action> actions = getPossibleMoves(terminal, currentSlot, height, container);
            System.out.println(actions.size());
            if (actions.isEmpty()) {
                return false;
            }
            for (Action action : actions) {
                result.add(action);
                action.execute(terminal);
                if (recursion(result, terminal, currentSlot, height, container)) {
                    return true;
                }
                result.remove(action);
                action.reverse(terminal);
            }
            //TODO: "false" correct?
            return false;
        }
        else {
            Action finalMove = new Action(container, currentSlot);
            finalMove.execute(terminal);
            result.add(finalMove);
            return true;
        }
    }

    public static List<Action> getPossibleMoves(Terminal terminal, Slot currentSlot, int height, Container container) {
        List<Action> actions = new ArrayList<>();

        Set<Container> movableBlocking = new HashSet<>();
        //blocking currentSlot + slots needed to place container
        for (int i = 0; i < container.length; i++) {
            Slot slot = terminal.slots.get(currentSlot.id + i);
            if (slot.containers.size() > height) {
                movableBlocking.addAll(getMovableBlockingContainers(slot.containers.get(height)));
            }
        }
        //blocking container
        movableBlocking.addAll(getMovableBlockingContainers(container));

        Set<Slot> blacklistSlots = new HashSet<>();
        for (Container blockingContainer : movableBlocking) {
            blacklistSlots.addAll(blockingContainer.slots);
        }

        //Per movable blocking container make action objects with all possible locations to move to
        for (Container blockingContainer : movableBlocking) {
            for (int y = 0; y < terminal.width; y++) {
                for (int x = 0; x + container.length <= terminal.length; x++) {
                    if (Algorithm.containerFits(terminal, new ArrayList<>(blacklistSlots), container, x, y)) {
                        actions.add(new Action(blockingContainer, terminal.area.get(x).get(y)));
                    }
                }
            }
        }

        return actions;
    }

    public static Set<Container> getMovableBlockingContainers(Container container) {
        Set<Container> blocking = new HashSet<>();

        for (Slot slot : container.slots) {
            //Check if containers above current
            if (slot.containers.peek() != container) {
                Container containerAbove = slot.containers.get(slot.containers.indexOf(container) + 1);
                if (containerAbove.isMovable()) {
                    blocking.add(containerAbove);
                }
                else {
                    blocking.addAll(getMovableBlockingContainers(containerAbove));
                }
            }
        }

        return blocking;
    }
}
