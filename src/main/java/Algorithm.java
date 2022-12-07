import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    public static void execute(List<Container> required, List<List<Slot>> area, List<Container> containers) {

        while(!required.isEmpty()){
            for(Container c: findExposed(area)){
                //haal c met kraan
                required.remove(c);
            }
        }
    }

    public static List<Container> findExposed(List<List<Slot>> area) {
        List<Container> res = new ArrayList<>();

        for (List<Slot> slotList : area) {
            for (Slot slot : slotList) {
                if (slot.containers.isEmpty()) {
                    continue;
                }
                Container topContainer = slot.containers.peek();
                if (res.contains(topContainer)) {
                    continue;
                }
                if (topContainer.checkMovable()) {
                    res.add(topContainer);
                }
            }
        }

        return res;
    }
}
