import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Crane {
    public final List<List<Slot>> area;
    public final int speedX;
    public final int speedY;
    public Coordinate location;
    public final Map<Integer, Coordinate> path = new HashMap<>();
    public Container attachedContainer;
    public int time;


    public Crane(List<List<Slot>> area, float X, float Y, int speedX, int speedY) {
        this.area = area;
        location = new Coordinate(X, Y);
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

