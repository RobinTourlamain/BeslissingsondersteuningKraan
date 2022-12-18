import java.util.Stack;

public class Slot {
    int id;
    int x;
    int y;
    int maxHeight;
    Stack<Container> containers;

    public Slot(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.containers = new Stack<>();
    }

    public void addContainer(Container c){
        containers.add(c);
    }

    @Override
    public String toString() {
        return "Slot{" +
                "id=" + id +
                '}';
    }
}
