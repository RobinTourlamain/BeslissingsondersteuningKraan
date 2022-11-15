import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Crane {
    private final List<List<Slot>> area;
    private final int speedX;
    private final int speedY;
    private Slot location;
    private final Map<Integer,Slot> path = new HashMap<>();
    private int time;


    public Crane(List<List<Slot>> area, int speedX, int speedY) {
        this.area = area;
        this.speedX = speedX;
        this.speedY = speedY;
        this.time = 0;
    }

    void setLocation(Slot location) {
        this.location = location;
        this.path.put(time,location);
        time++;
    }

    Slot getLocation() {
        return location;
    }

    Slot move(Slot dest) {
        int x;
        int y;

        if(dest.x - location.x > 0){
            x = location.x + speedX;
            if (x > dest.x) {
                x = dest.x;
            }
        }
        else{
            x = location.x - speedX;
            if (x < dest.x) {
                x = dest.x;
            }
        }
        if(dest.y - location.y > 0){
            y = location.y + speedY;
            if (y > dest.y) {
                y = dest.y;
            }
        }
        else{
            y = location.y - speedY;
            if (y < dest.y) {
                y = dest.y;
            }
        }

        path.put(time, area.get(x).get(y));
        time++;                             //tijd misrekent nog globaal

        location = area.get(x).get(y);
        return location;
    }
    public void printPath(){
        for(var entry: path.entrySet()){
            System.out.println("time: " + entry.getKey());
            int[][] depot = new int[area.size()][area.get(0).size()];
            depot[entry.getValue().x][entry.getValue().y] = 1;
            for(int i = area.get(0).size()-1; i >= 0 ; i--){
                for(int j = 0; j< area.size(); j++){
                    System.out.print(" [" + depot[j][i] + "] ");
                }
                System.out.println();
            }
        }
    }
}

