package com.example.beslissingsondersteuningkraan;

import java.util.*;

public class HeightReduction {

    public static List<Action> makeSolution(Terminal terminal) {
        List<Action> result = new ArrayList<>();

        recursion(result, terminal);

        return result;
    }

    public static boolean recursion(List<Action> result, Terminal terminal) {

        List<Container> containersToMove = Algorithm.findContainersAboveMaxHeight(terminal);
        List<Crane> cranes = terminal.cranes;

        if (containersToMove.isEmpty()) return true;

        for (Crane crane : cranes) {
            for (Container container : containersToMove) {
                if (containerInReach(crane, container)) {
                    if (!moveContainer(result, terminal, crane , container)) {
                        break;
                    }
                    if (recursion(result, terminal)) {
                        return true;
                    }
                    Action action = result.remove(result.size() - 1);
                    action.reverse(terminal);
                }
            }
        }
        return false;
    }

    public static boolean moveContainer(List<Action> result, Terminal terminal, Crane crane, Container container) {
        if (!container.isMovable()) return false;
        Slot slot = craneHasRoomForContainer(terminal, crane, container, new ArrayList<>());
        if (slot != null) {
            Action action = new Action(container, terminal.area.get(slot.x).get(slot.y));
            action.execute(terminal);
            result.add(action);
            System.out.println(action.container.id + " to " + action.slot.id);
            return true;
        }
        System.out.println("no room!");
        int room = otherCranesHaveRoom(terminal, crane, container);
        if (room == 0) {
            System.out.println("maakplaats!");
            return makeRoomHere(result, terminal, crane, container);
        }
        else if (!isInTransition(terminal, crane, container, room)) {
            return moveToTransitionZone(result, terminal, crane, container, room);
        }

        return false;
    }

    public static boolean makeRoomHere(List<Action> result, Terminal terminal, Crane crane, Container container) {

        boolean resultFound = false;
        int depth = 1;
        while (!resultFound && depth <= 50) {
            resultFound = makeRoomRecursion(result, terminal, crane, container, depth);
            depth++;
        }
        return resultFound;
    }

    public static boolean makeRoomRecursion(List<Action> result, Terminal terminal, Crane crane, Container container, int depth) {
        Slot slot = craneHasRoomForContainer(terminal, crane, container, new ArrayList<>());
        if (slot != null) {
            return true;
        }
        if (depth <= 0) {
            return false;
        }
        List<Action> actions = getMoves(terminal, crane);
        if (actions.isEmpty()) {
            return false;
        }
        //Collections.shuffle(actions);
        for (Action action : actions) {
            result.add(action);
            action.execute(terminal);
            if (makeRoomRecursion(result, terminal, crane, container, depth - 1)) {
                return true;
            }
            result.remove(action);
            action.reverse(terminal);
        }
        return false;
    }

    public static List<Action> getMoves(Terminal terminal, Crane crane) {
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
            Slot containerSlot = movableContainer.slots.get(0);
            if (!(crane.xMin <= containerSlot.x && containerSlot.x + movableContainer.length <= crane.xMax)) {
                continue;
            }
            for (int y = 0; y < terminal.width; y++) {
                for (int x = crane.xMin; x + movableContainer.length < crane.xMax; x++) {
                    if (Algorithm.containerFitsHeight(terminal, new ArrayList<>(), movableContainer, x, y)) {
                        moveActions.add(new Action(movableContainer, terminal.area.get(x).get(y)));
                    }
                }
            }
        }

        return moveActions;
    }

    public static boolean isInTransition(Terminal terminal, Crane crane, Container container, int room) {
        Crane secondcrane = terminal.cranes.get(crane.id + room);
        int minX = Math.max(crane.xMin, secondcrane.xMin);
        int maxX = Math.min(crane.xMax, secondcrane.xMax);

        return container.slots.get(0).x > minX && container.slots.get(0).x + container.length - 1 < maxX;
    }

    public  static boolean moveToTransitionZone(List<Action> result, Terminal terminal, Crane crane, Container container, int room) {
        Crane secondCrane = terminal.cranes.get(crane.id + room);
        int minX = Math.max(crane.xMin, secondCrane.xMin);
        int maxX = Math.min(crane.xMax, secondCrane.xMax);

        //probeer in transition te zetten
        for (int y = 0; y < terminal.width; y++) {
            for (int x = minX; x + container.length - 1 <= maxX; x++) {
                List<Slot> slots = new ArrayList<>();
                for (int i = 0; i<container.length; i++) {
                    slots.add(terminal.area.get(x+i).get(y));
                }
                if (container.isPlaceable(slots)) {
                    System.out.println("plaats in transition");
                    Action action = new Action(container, terminal.area.get(x).get(y));
                    action.execute(terminal);
                    result.add(action);
                    return true;
                }
            }
        }
        //TODO maak transition vrij?

        return false;
    }

    //return -1 voor kraan links heeft plaats, 0 voor geen plaats, 1 voor rechts heeft plaats
    public static int otherCranesHaveRoom(Terminal terminal, Crane notcrane, Container container) {
        List<Crane> cranes = new ArrayList<>(terminal.cranes);
        cranes.remove(notcrane);
        for (Crane crane : cranes) {
            if (craneHasRoomForContainer(terminal,crane,container, new ArrayList<>()) != null) {
                if (crane.id < notcrane.id) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
        }
        System.out.println("other crane has room");
        return 0;
    }

    public static Slot craneHasRoomForContainer(Terminal terminal, Crane crane, Container container, List<Slot> blacklist) {
        for (int y = 0; y < terminal.width; y++) {
            //x + container.length might need -1
            for (int x = crane.xMin; x + container.length - 1 < crane.xMax; x++) { //kan fucken als de xmax van de kraan niet klopt >:(
                List<Slot> slots = new ArrayList<>();
                for (int i = 0; i < container.length; i++) {
                    slots.add(terminal.area.get(x+i).get(y));
                }
                boolean valid = true;
                for (Slot slot : slots) {
                    if (blacklist.contains(slot)) {
                        valid = false;
                        break;
                    }
                }
                if (valid && container.isPlaceableTargetHeight(slots, terminal.targetHeight)) {
                    return terminal.area.get(x).get(y);
                }
            }
        }
        return null;
    }

    public static boolean containerInReach(Crane crane, Container container) {
        int leftMostSlot = container.slots.get(0).x;
        int rightMostSlot = leftMostSlot + container.length - 1;

        if (container.length > 1) {
            leftMostSlot++;
            rightMostSlot--;
        }

        return crane.xMin <= leftMostSlot && rightMostSlot <= crane.xMax;
    }
}
