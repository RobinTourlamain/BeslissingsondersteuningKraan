import java.util.HashMap;
import java.util.Map;

public class Crane {
    private Coordinaat[][][] area;
    private final int speedX;
    private final int speedY;
    private Coordinaat location;
    private Map<Integer,Coordinaat> path = new HashMap<>();
    private int time;

    public Crane(Coordinaat[][][] area, int speedX, int speedY) {
        this.area = area;
        this.speedX = speedX;
        this.speedY = speedY;
        this.time = 0;
    }

    void setLocation(Coordinaat location) {
        this.location = location;
    }

    Coordinaat getLocation() {
        return location;
    }

//    Coordinaat move(Coordinaat dest) {
//        int totalTime = Integer.max(Math.abs(location.getX() - dest.getX()) / speedX , Math.abs(location.getY() - dest.getY()) / speedY);
//        int x;
//        int y;
//        for (int i = 0; i <= totalTime; i++) {
//            x = location.getX() + speedX * i;
//            y = location.getY() + speedY * i;
//            if (x > dest.getX()) {
//                x = dest.getX();
//            }
//            if (y > dest.getY()) {
//                y = dest.getY();
//            }
//            path.put(time, area[x][y][0]);
//            time++;
//        }
//        location = area[dest.getX()][dest.getY()][0];
//        return location;
//    }
    Coordinaat move(Coordinaat dest) {
        int x;
        int y;

        if(dest.getX()-location.getX() > 0){
            x = location.getX() + speedX;
            if (x > dest.getX()) {
                x = dest.getX();
            }
        }
        else{
            x = location.getX() - speedX;
            if (x < dest.getX()) {
                x = dest.getX();
            }
        }
        if(dest.getY()- location.getY() > 0){
            y = location.getY() + speedY;
            if (y > dest.getY()) {
                y = dest.getY();
            }
        }
        else{
            y = location.getY() - speedY;
            if (y < dest.getY()) {
                y = dest.getY();
            }
        }

        path.put(time, area[x][y][0]);
        time++;

        location = area[x][y][0];
        return location;
    }
    public void printPath(){
        for(var entry: path.entrySet()){
            System.out.println("time: " + entry.getKey());
            int[][] depot = new int[area.length][area[0].length];
            depot[entry.getValue().getX()][entry.getValue().getY()] = 1;
            for(int i = area.length-1; i>=0 ; i--){
                for(int j = 0; j< area[0].length; j++){
                    System.out.print(" [" + depot[i][j] + "] ");
                }
                System.out.println();
            }
        }
    }
}

