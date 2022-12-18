import java.util.Stack;

public class Slot {
    int id;
    int x;
    int y;
    int maxHeight;
    Stack<Container> containers;

    public Slot(int id, int x, int y, int maxHeight) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.maxHeight = maxHeight;
        this.containers = new Stack<>();
        for (int i = 0; i < maxHeight; i++) {
            containers.add(null);
        }
    }

    public void addContainer(Container container){
        containers.add(container);
    }

    public void addContainer(Container container, int height) {
        containers.remove(height);
        containers.add(height, container);
    }

    @Override
    public String toString() {
        return "Slot{" +
                "id=" + id +
                '}';
    }
}
