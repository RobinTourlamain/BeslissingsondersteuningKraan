import java.util.*;

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
            //System.out.println("recursie");
            //TODO: when should it return false?
            List<Action> actions = getPossibleMoves(terminal, currentSlot, height, container);
            //System.out.println(actions.size());
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
            if (Algorithm.staysWithinOneCraneArea(terminal, container, currentSlot) || terminal.cranes.size() == 1) {
                Action finalMove = new Action(container, currentSlot);
                finalMove.execute(terminal);
                result.add(finalMove);
                System.out.println(container.id + " moved successfully");
                return true;
            }
            else {
                result.addAll(makeTransferPossible(terminal, container, currentSlot));
                return true;
            }
        }
    }

    public static List<Action> makeTransferPossible(Terminal terminal, Container container, Slot currentSlot) {
        List<Action> result = new ArrayList<>();

        List<Crane> craneCopy = new ArrayList<>(terminal.cranes);
        craneCopy.sort(Comparator.comparing(crane -> crane.xMin));

        int xMin = craneCopy.get(1).xMin;
        int xMax = craneCopy.get(0).xMax;

//        if (container.length > 1) {
//            xMin -= 1;
//            xMax += 1;
//        }

        transferRecursion(result, terminal, container, currentSlot, xMin, xMax);

        return result;
    }

    public static boolean transferRecursion(List<Action> result, Terminal terminal, Container container, Slot currentSlot, int xMin, int xMax) {
        Slot transferSlot = Algorithm.findTransferSlot(terminal, container);
        if (transferSlot == null) {
            List<Action> actions = getPossibleTransferMoves(terminal, container, xMin, xMax);
            if (actions.isEmpty()) {
                return false;
            }
            for (Action action : actions) {
                result.add(action);
                action.execute(terminal);
                if (transferRecursion(result, terminal, container, currentSlot, xMin, xMax)) {
                    Action restore = new Action(action.container, action.prevSlot);
                    result.add(restore);
                    restore.execute(terminal);

                    return true;
                }
                result.remove(action);
                action.reverse(terminal);
            }
            return false;
        }
        else {
            Action transferMove = new Action(container, transferSlot);
            transferMove.execute(terminal);
            result.add(transferMove);
            System.out.println("transfer");
            Action finalMove = new Action(container, currentSlot);
            finalMove.execute(terminal);
            result.add(finalMove);
            System.out.println(container.id + " moved successfully");
            return true;
        }
    }

    public static List<Action> getPossibleTransferMoves(Terminal terminal, Container container, int xMin, int xMax) {
        List<Action> actions = new ArrayList<>();

        Set<Container> movableContainers = new HashSet<>();

        for (int y = 0; y < terminal.width; y++) {
            for (int x = xMin; x + container.length < xMax; x++) {
                Slot slot = terminal.area.get(x).get(y);
                if (slot.containers.size() != 0) {
                    Container topContainer = slot.containers.peek();
                    if (topContainer.isMovable()) {
                        movableContainers.add(topContainer);
                    }
                }
            }
        }


        for (Container movableContainer : movableContainers) {
            for (int y = 0; y < terminal.width; y++) {
                for (int x = 0; x + container.length <= terminal.length; x++) {
                    if (Algorithm.containerFits(terminal, new ArrayList<>(), container, x, y)) {
                        actions.add(new Action(movableContainer, terminal.area.get(x).get(y)));
                    }
                }
            }
        }
        Collections.shuffle(actions);

        return actions;
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

//        //Move containers in transferzone
//        if (terminal.cranes.size() > 1) {
//            List<Crane> craneCopy = new ArrayList<>(terminal.cranes);
//            craneCopy.sort(Comparator.comparing(crane -> crane.xMin));
//
//            int xMin = terminal.cranes.get(1).xMin;
//            int xMax = terminal.cranes.get(0).xMax;
//
//            Set<Container> movableContainers = new HashSet<>();
//
//            for (int y = 0; y < terminal.width; y++) {
//                for (int x = xMin; x + container.length < xMax; x++) {
//                    Slot slot = terminal.area.get(x).get(y);
//                    if (slot.containers.size() != 0) {
//                        Container topContainer = slot.containers.peek();
//                        if (topContainer.isMovable()) {
//                            movableContainers.add(topContainer);
//                        }
//                    }
//                }
//            }
//
//            List<Action> transferActionList = new ArrayList<>();
//
//            for (Container movableContainer : movableContainers) {
//                for (int y = 0; y < terminal.width; y++) {
//                    for (int x = 0; x + container.length <= terminal.length; x++) {
//                        if (Algorithm.containerFits(terminal, new ArrayList<>(blacklistSlots), container, x, y)) {
//                            transferActionList.add(new Action(movableContainer, terminal.area.get(x).get(y)));
//                        }
//                    }
//                }
//            }
//            Collections.shuffle(transferActionList);
//
//            actions.addAll(transferActionList);
//        }

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
