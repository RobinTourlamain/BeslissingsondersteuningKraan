import javax.print.attribute.standard.PresentationDirection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HeightReduction {

    public static List<Action> makeSolution(Terminal terminal){
        List<Action> result = new ArrayList<>();

        recursion(terminal, result);

        return result;
    }

    public static boolean recursion(Terminal terminal, List<Action> result){

        List<Container> containersToMove = Algorithm.findContainersAboveMaxHeight(terminal);
        List<Crane> cranes = terminal.cranes;

        if(containersToMove.isEmpty()) return true;

        for(Crane crane : cranes){
            for(Container container : containersToMove){
                if(containerInReach(crane, container)){
                    if(!moveContainer(terminal, crane , container, result)) break;
                    if(recursion(terminal, result)){
                        return true;
                    }
                    else{
                        Action action = result.remove(result.size()-1);
                        action.reverse(terminal);
                    }
                }
            }
        }

        return false;
    }

    public static boolean moveContainer(Terminal terminal, Crane crane, Container container, List<Action> result){
        if(!container.isMovable()) return false;
        int[] coords = craneHasRoomForContainer(terminal, crane, container);
        if(coords.length > 1){
            System.out.println("room!");
            Action action = new Action(container, terminal.area.get(coords[0]).get(coords[1]));
            action.execute(terminal);
            result.add(action);
            System.out.println(action.container.id + " to " + action.slot.id);
            return true;
        }
        System.out.println("no room!");
        if(!otherCranesHaveRoom(terminal, crane, container)){
            //TODO maak hierplaats
        }
        else{

        }

        return false;
    }

    public static boolean otherCranesHaveRoom(Terminal terminal, Crane notcrane, Container container){
        List<Crane> cranes = new ArrayList<>(terminal.cranes);
        cranes.remove(notcrane);
        for(Crane crane : cranes){
            if(craneHasRoomForContainer(terminal,crane,container).length > 1){
                return true;
            }
        }
        return false;
    }
    //returned [x, y] om opnieuw zoeken uit te sparen
    public static int[] craneHasRoomForContainer(Terminal terminal, Crane crane, Container container){
        for(int y = 0; y < terminal.width; y++){
            for(int x = crane.xMin; x + container.length -1 < crane.xMax; x++){ //kan fucken als de xmax van de kraan niet klopt >:(
                List<Slot> slots = new ArrayList<>();
                for(int i = 0; i<container.length; i++){
                    slots.add(terminal.area.get(x+i).get(y));
                }
                if(container.isPlaceableTargetHeight(slots, terminal.targetHeight)){
                    int[] res = new int[2];
                    res[0] = x;
                    res[1] = y;
                    return res;
                }
            }
        }
        return new int[0];
    }

    public static boolean containerInReach(Crane crane, Container container){
        List<Integer> slots = container.slots.stream().map(slot -> slot.x).toList();
        int leftmostslot = Collections.min(slots);
        int rightmostslot = leftmostslot + container.length - 1;

        if(crane.xMin <= leftmostslot && crane.xMax >= rightmostslot){
            return true;
        }
        return false;
    }
}
