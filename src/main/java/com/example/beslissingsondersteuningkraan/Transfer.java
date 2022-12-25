package com.example.beslissingsondersteuningkraan;

import java.util.*;

public class Transfer {

    public static List<Action> makeSolution(Terminal terminal, Slot currentSlot, int height, Container container) {
        List<Action> result = new ArrayList<>();

        boolean resultFound = false;
        int depth = 1;
        while (!resultFound && depth <= 50) {
            resultFound = recursion(result, terminal, currentSlot, height, container, depth);
            depth++;
        }

        return result;
    }

    public static boolean recursion(List<Action> result, Terminal terminal, Slot currentSlot, int height, Container container, int depth) {
        boolean containerPlaceable = true;
        for (int i = 0; i < container.length; i++) {
            Slot slot = terminal.slots.get(currentSlot.id + i);
            if (slot.containers.size() > height) {
                containerPlaceable = false;
                break;
            }
        }

        if (!container.isMovable() || !containerPlaceable) {
            if (depth <= 0) {
                return false;
            }

            List<Action> actions = getPossibleMoves(terminal, currentSlot, height, container);
            System.out.println(actions.size());
            if (actions.isEmpty()) {
                return false;
            }

            for (Action action : actions) {
                result.add(action);
                action.execute(terminal);
                if (recursion(result, terminal, currentSlot, height, container, depth - 1)) {
                    return true;
                }
                result.remove(action);
                action.reverse(terminal);
            }

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
                for (int x = 0; x + blockingContainer.length < terminal.length; x++) {
                    if (Algorithm.containerFits(terminal, new ArrayList<>(blacklistSlots), container, x, y)) {
                        actions.add(new Action(blockingContainer, terminal.area.get(x).get(y)));
                    }
                }
            }
        }


        //Generate other random moves if stuck
        Set<Container> movableContainers = new HashSet<>();
        for (int y = 0; y < terminal.width; y++) {
            for (int x = 0; x < terminal.length; x++) {
                Slot slot = terminal.area.get(x).get(y);
                if (slot.containers.size() != 0) {
                    Container topContainer = slot.containers.peek();
                    if (topContainer.isMovable()) {
                        movableContainers.add(topContainer);
                    }
                }
            }
        }

        List<Action> moveActions = new ArrayList<>();
        for (Container movableContainer : movableContainers) {
            int xMin = 0;
            int xMax = terminal.length;

            if (terminal.cranes.size() > 1) {
                for (Crane crane : terminal.cranes) {
                    Slot containerSlot = movableContainer.slots.get(0);
                    if (crane.xMin <= containerSlot.x && containerSlot.x + movableContainer.length <= crane.xMax) {
                        xMin = crane.xMin;
                        xMax = crane.xMax;
                        break;
                    }
                }
            }

            for (int y = 0; y < terminal.width; y++) {
                for (int x = xMin; x + movableContainer.length < xMax; x++) {
                    if (Algorithm.containerFits(terminal, new ArrayList<>(blacklistSlots), movableContainer, x, y)) {
                        moveActions.add(new Action(movableContainer, terminal.area.get(x).get(y)));
                    }
                }
            }
        }
        actions.addAll(moveActions);

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

    public static List<Action> makeTransferPossible(Terminal terminal, Container container, Slot currentSlot) {
        List<Action> result = new ArrayList<>();

        List<Crane> craneCopy = new ArrayList<>(terminal.cranes);
        craneCopy.sort(Comparator.comparing(crane -> crane.xMin));

        int xMin = craneCopy.get(1).xMin;
        int xMax = craneCopy.get(0).xMax;

        boolean resultFound = false;
        int depth = 1;
        while (!resultFound && depth <= 50) {
            resultFound = transferRecursion(result, terminal, container, currentSlot, xMin, xMax, depth);
            depth++;
        }

        return result;
    }

    public static boolean transferRecursion(List<Action> result, Terminal terminal, Container container, Slot currentSlot, int xMin, int xMax, int depth) {
        Slot transferSlot = Algorithm.findTransferSlot(terminal, container);
        if (transferSlot == null) {
            if (depth <= 0) {
                return false;
            }
            List<Action> actions = getPossibleTransferMoves(terminal, container, xMin, xMax);
            if (actions.isEmpty()) {
                return false;
            }
            for (Action action : actions) {
                result.add(action);
                action.execute(terminal);
                if (transferRecursion(result, terminal, container, currentSlot, xMin, xMax, depth - 1)) {
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
            for (int x = xMin; x < xMax; x++) {
                Slot slot = terminal.area.get(x).get(y);
                if (slot.containers.size() == 0) {
                    continue;
                }
                Container topContainer = slot.containers.peek();
                if (x + topContainer.length < xMax && topContainer.isMovable()) {
                    movableContainers.add(topContainer);
                }
            }
        }

        for (Container movableContainer : movableContainers) {
            for (int y = 0; y < terminal.width; y++) {
                for (int x = 0; x + movableContainer.length < terminal.length; x++) {
                    if (Algorithm.containerFits(terminal, new ArrayList<>(), container, x, y)) {
                        actions.add(new Action(movableContainer, terminal.area.get(x).get(y)));
                    }
                }
            }
        }

        return actions;
    }
}
