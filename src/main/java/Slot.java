import java.util.ArrayDeque;
import java.util.Deque;

public class Slot {
    int id;
    int x;
    int y;
    int Hmax;
    Deque<Container> containers;

    public Slot(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.containers = new ArrayDeque<>();
    }

}
