import java.util.*;

public class ActionToOutput {

    public static List<Map<Integer, Action>> toOutput(List<Action> actionlist, List<Crane> cranes) {

        List<Action> actions = new ArrayList<>(actionlist);
        List<Map<Integer, Action>> actionframes = new ArrayList<>();

        //bepaal welke acties in parallel gebeuren
        int index = 0;
        while (!actions.isEmpty()) {
            actionframes.add(new HashMap<>());
            for (Crane crane : cranes) {
                for (int i = 0; i < actions.size(); i++) {
                    Action action = actions.get(i);
                    if (canExecute(crane, action) && !overlapWithOtherActions(action, actionframes.get(index), actions, i)) {
                        actionframes.get(index).put(crane.id, action);
                        actions.remove(action);
                        break;
                    }
                }
            }
            index++;
        }

        actionframes.forEach(m -> {m.forEach((key, value)-> {
            System.out.print("crane " + key + " container " + value.container.id + " from x " + value.prevSlot.x + " to x " + value.slot.x + ", ");
        });
            System.out.println();});
        return actionframes;
    }

    public static boolean overlapWithOtherActions(Action action, Map<Integer, Action> craneactions, List<Action> actions, int index) {

        if (craneactions.isEmpty()) {
            return false;
        }

        List<Action> consider = new ArrayList<>(craneactions.values());
        for (int i = 0; i < index; i++) {
            consider.add(actions.get(i));
        }

        List<Integer> slots = new ArrayList<>();
        slots.add(action.slot.x);
        slots.add(action.slot.x + action.container.length - 1);
        slots.add(action.prevSlot.x);
        slots.add(action.prevSlot.x + action.container.length - 1);
        int leftmostslot = Collections.min(slots);
        int rightmostslot = Collections.max(slots);



        for (Action a : consider) {
            List<Integer> compareslots = new ArrayList<>();
            compareslots.add(a.slot.x);
            compareslots.add(a.slot.x + a.container.length - 1);
            compareslots.add(a.prevSlot.x);
            compareslots.add(a.prevSlot.x + a.container.length - 1);
            int leftmostslotc = Collections.min(compareslots);
            int rightmostslotc = Collections.max(compareslots);

            if (leftmostslot <= rightmostslotc && rightmostslot >= leftmostslotc) {
                System.out.println("overlap");
                return true;
            }
        }
        return false;
    }

    public static boolean canExecute(Crane crane, Action action) {
        List<Integer> slots = new ArrayList<>();
        slots.add(action.slot.x);
        slots.add(action.slot.x + action.container.length - 1);
        slots.add(action.prevSlot.x);
        slots.add(action.prevSlot.x + action.container.length - 1);

        int leftmostslot = Collections.min(slots);
        int rightmostslot = Collections.max(slots);

        if (action.container.length > 1) {
            leftmostslot++;
            rightmostslot--;
        }

        if (crane.xMin <= leftmostslot && rightmostslot <= crane.xMax) {
            return true;
        }
        System.out.println("kan niet executen");
        System.out.println(leftmostslot + "," + rightmostslot + " niet binnen: " + crane.xMin + "," + crane.xMax + "length: " + action.container.length);
        return false;
    }

}
