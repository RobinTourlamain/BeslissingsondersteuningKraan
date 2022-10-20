import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Depot {
    private List<Crane> cranes = new ArrayList<>();
    private Coordinaat[][][] area;
    private Map<Crane, List<Coordinaat>> moves = new HashMap<>();
    private int minDist;

    public Depot(int x, int y, int z, List<Crane> cranes, int minDist) {
        this.area = new Coordinaat[x][y][z];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                for (int k = 0; k < z; k++) {
                    area[i][j][k] = new Coordinaat(i, j, k);
                }
            }
        }
        for(Crane c: cranes){
            moves.put(c, new ArrayList<>());
        }
        this.cranes = cranes;
        this.minDist = minDist;
    }

    public void addMove(int x, int y, Crane c){
        moves.get(c).add(area[x][y][0]);
    }

    public void calcMovements() {
        boolean done = false;
        int time = 0;
        while(!done){
            List<Coordinaat> plan = new ArrayList<>();

            for(Crane c: cranes){                   //stationaire kranen blijven staan
                if(moves.get(c).isEmpty()){
                    plan.add(c.getLocation());
                }
            }

            for(Crane c: cranes){
                if(!moves.get(c).isEmpty()) {

                    Coordinaat current = c.getLocation();
                    Coordinaat next = c.move(moves.get(c).get(0));
                    for(Coordinaat coordinaat : plan) {                                    //for grootte plan
                        if (Math.abs(next.getX() - coordinaat.getX()) <= minDist) {           //bekijk minimum distance tussen onze next en nexten in plan
                            next = current;
                            c.setLocation(next);
                            break;
                        }
                    }
                    plan.add(next);             //anders onze next vorige locatie maken en in plan steken

                    if(moves.get(c).get(0).getX() == c.getLocation().getX() && moves.get(c).get(0).getY() == c.getLocation().getY()){
                        moves.get(c).remove(0);
                    }
                }

            }

            time++;
            done = true;
            for (var entry: moves.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    done = false;
                    break;
                }
            }
        }
    }
    public void addCrane(Crane c){
        cranes.add(c);
        moves.put(c,new ArrayList<>());
    }
    public void removeCrane(Crane c){
        cranes.remove(c);
    }
}
