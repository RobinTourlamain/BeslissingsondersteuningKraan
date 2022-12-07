import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Crane {

    public int id;
    public float x;
    public float y;
    public int xMin;
    public int xMax;
    public int yMin;
    public int yMax;
    public final int speedX;
    public final int speedY;
    public Coordinate location;
    public final Map<Integer, Coordinate> path = new HashMap<>();
    public Container attachedContainer;
    public int time;


    public Crane(int id, float X, float Y, int xMin, int xMax, int yMin, int yMax, int speedX, int speedY) {
        this.id = id;
        this.location = new Coordinate(X, Y);
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.speedX = speedX;
        this.speedY = speedY;
        attachedContainer = null;
        this.time = 0;
    }

    void setLocation(Coordinate location) {
        this.location = location;
        this.path.put(time, location);
        time++;
    }

    Coordinate getLocation() {
        return location;
    }

    Coordinate move(Coordinate dest) {
        float x;
        float y;

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

        Coordinate coordinate = new Coordinate(x, y);

        path.put(time, coordinate);
        //tijd misrekent nog globaal
        time++;

        location = coordinate;
        return location;
    }

    public void attachContainer(Container container) {
        this.attachedContainer = container;
    }

    public void detachContianer() {
        this.attachedContainer = null;
    }
}

