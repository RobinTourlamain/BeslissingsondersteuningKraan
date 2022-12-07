import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Terminal {
    public String name;
    public int length;
    public int width;
    public int maxHeight;
    public final List<Crane> cranes;
    public final List<List<Slot>> area;
    public final Map<Crane, List<Slot>> moves = new HashMap<>();

    public Terminal(String name, int length, int width, int maxHeight, List<List<Slot>> area, List<Crane> cranes) {
        this.area = area;
        for(Crane c: cranes){
            moves.put(c, new ArrayList<>());
        }
        this.cranes = cranes;
    }

    public void addMove(int x, int y, Crane c) {
        moves.get(c).add(area.get(x).get(y));
    }

//    public void calcMovements() {
//        boolean done = false;
//        int time = 0;
//        while(!done){
//            List<Slot> plan = new ArrayList<>();
//
//            for(Crane c: cranes){                   //stationaire kranen blijven staan
//                if(moves.get(c).isEmpty()){
//                    plan.add(c.getLocation());
//                }
//            }
//
//            for(Crane crane: cranes) {
//                if(!moves.get(crane).isEmpty()) {
//
//                    Slot current = crane.getLocation();
//                    Slot next = crane.move(moves.get(crane).get(0));
//                    for(Slot coordinaat : plan) {                                    //for grootte plan
//                        if (Math.abs(next.x - coordinaat.x) <= minDist) {           //bekijk minimum distance tussen onze next en nexten in plan
//                            next = current;
//                            crane.setLocation(next);
//                            break;
//                        }
//                    }
//                    plan.add(next);             //anders onze next vorige locatie maken en in plan steken
//
//                    if(moves.get(crane).get(0).x == crane.getLocation().x && moves.get(crane).get(0).y == crane.getLocation().y){
//                        moves.get(crane).remove(0);
//                    }
//                }
//
//            }
//
//            time++;
//            done = true;
//            for (var entry: moves.entrySet()) {
//                if (!entry.getValue().isEmpty()) {
//                    done = false;
//                    break;
//                }
//            }
//        }
//    }

    public void addCrane(Crane c){
        cranes.add(c);
        moves.put(c,new ArrayList<>());
    }

    public void removeCrane(Crane c){
        cranes.remove(c);
    }
}
