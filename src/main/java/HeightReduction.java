import javax.print.attribute.standard.PresentationDirection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HeightReduction {

    public static List<Action> makeSolution(Terminal terminal) {
        List<Action> result = new ArrayList<>();

        recursion(terminal, result);

        return result;
    }

    public static boolean recursion(Terminal terminal, List<Action> result) {

        List<Container> containersToMove = Algorithm.findContainersAboveMaxHeight(terminal);
        List<Crane> cranes = terminal.cranes;

        if (containersToMove.isEmpty()) return true;

        for (Crane crane : cranes) {
            for (Container container : containersToMove) {
                if (containerInReach(crane, container)) {
                    if (!moveContainer(terminal, crane , container, result)) break;
                    if (recursion(terminal, result)) {
                        return true;
                    }
                    else {
                        Action action = result.remove(result.size()-1);
                        action.reverse(terminal);
                    }
                }
            }
        }
        return false;
    }

    public static boolean moveContainer(Terminal terminal, Crane crane, Container container, List<Action> result) {
        if (!container.isMovable()) return false;
        int[] coords = craneHasRoomForContainer(terminal, crane, container, new ArrayList<>());
        if (coords.length > 1) {
            Action action = new Action(container, terminal.area.get(coords[0]).get(coords[1]));
            action.execute(terminal);
            result.add(action);
            System.out.println(action.container.id + " to " + action.slot.id);
            return true;
        }
        System.out.println("no room!");
        int room = otherCranesHaveRoom(terminal, crane, container);
        if (room == 0) {
            System.out.println("maakplaats!");
            return makeRoomHere(terminal, crane, container, result);
        }
        else if (!isInTransition(terminal, crane, container, room)) {
            return moveToTransitionZone(terminal, crane, container, result, room);
        }

        return false;
    }

    public static boolean makeRoomHere(Terminal terminal, Crane crane, Container container, List<Action> result) {
        for (int y = 0; y < terminal.width; y++) {
            for (int x = crane.xMin; x + container.length -1 < crane.xMax; x++) {
                if (!terminal.area.get(x).get(y).containers.isEmpty()) {
                    if (terminal.area.get(x).get(y).containers.peek().isMovable()) {
                        if (moveRandom(terminal, terminal.area.get(x).get(y).containers.peek(), crane, result)) {
                            int[] coords = craneHasRoomForContainer(terminal, crane, container, new ArrayList<>());
                            if (coords.length > 1) {
                                Action action = new Action(container, terminal.area.get(coords[0]).get(coords[1]));
                                action.execute(terminal);
                                result.add(action);
                                System.out.println(action.container.id + " to " + action.slot.id);
                                return true;
                            }
                            else if (makeRoomHere(terminal, crane, container, result)) {
                                return true;
                            }
                            else {
                                Action action = result.remove(result.size()-1);
                                action.reverse(terminal);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean moveRandom(Terminal terminal, Container container, Crane crane, List<Action> result) {
        List<Slot> blacklist = new ArrayList<>(container.slots);
        int[] coords = craneHasRoomForContainer(terminal, crane, container, blacklist);
        if (coords.length > 1) {
            Action action = new Action(container, terminal.area.get(coords[0]).get(coords[1]));
            action.execute(terminal);
            result.add(action);
            System.out.println(action.container.id + " to " + action.slot.id);
        }
        return false;
    }

    public static boolean isInTransition(Terminal terminal, Crane crane, Container container, int room) {
        Crane secondcrane = terminal.cranes.get(crane.id + room);
        int minx = Math.max(crane.xMin, secondcrane.xMin);
        int maxx = Math.min(crane.xMax, secondcrane.xMax);

        return container.slots.get(0).x > minx && container.slots.get(0).x + container.length - 1 < maxx;
    }

    public  static boolean moveToTransitionZone(Terminal terminal, Crane crane, Container container, List<Action> result, int room) {
        Crane secondcrane = terminal.cranes.get(crane.id + room);
        int minx = Math.max(crane.xMin, secondcrane.xMin);
        int maxx = Math.min(crane.xMax, secondcrane.xMax);

        //probeer in transition te zetten
        for (int y = 0; y < terminal.width; y++) {
            for (int x = minx; x + container.length - 1 < maxx; x++) {
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
            if (craneHasRoomForContainer(terminal,crane,container, new ArrayList<>()).length > 1) {
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

    //returned [x, y] om opnieuw zoeken uit te sparen
    public static int[] craneHasRoomForContainer(Terminal terminal, Crane crane, Container container, List<Slot> blacklist) {
        for (int y = 0; y < terminal.width; y++) {
            for (int x = crane.xMin; x + container.length -1 < crane.xMax; x++) { //kan fucken als de xmax van de kraan niet klopt >:(
                List<Slot> slots = new ArrayList<>();
                for (int i = 0; i<container.length; i++) {
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
                    int[] res = new int[2];
                    res[0] = x;
                    res[1] = y;
                    return res;
                }
            }
        }
        return new int[0];
    }

    public static boolean containerInReach(Crane crane, Container container) {
        List<Integer> slots = container.slots.stream().map(slot -> slot.x).toList();
        int leftmostslot = Collections.min(slots);
        int rightmostslot = leftmostslot + container.length - 1;

        return crane.xMin <= leftmostslot && crane.xMax >= rightmostslot;
    }
}
