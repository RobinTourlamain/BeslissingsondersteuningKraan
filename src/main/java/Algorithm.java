import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Algorithm {

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

    public static List<Integer> findChangedContainerIds(Terminal terminal, Terminal targetTerminal) {

        List<Integer> movedContainerIds = new ArrayList<>();

        for (int i = 0; i < terminal.containers.size(); i++) {
            if (terminal.containers.get(i).slots.get(0).id != targetTerminal.containers.get(i).slots.get(0).id) {
                movedContainerIds.add(terminal.containers.get(i).id);
            }
        }

        return movedContainerIds;
    }

    public static List<Integer> findContainerIdsAboveMaxHeight(Terminal terminal) {
        Set<Integer> containerIdsToMove = new HashSet<>();

        for (Slot slot : terminal.slots) {
            if (slot.containers.size() > terminal.targetHeight) {
                containerIdsToMove.add(slot.containers.peek().id);
            }
        }

        return new ArrayList<>(containerIdsToMove);
    }
}
