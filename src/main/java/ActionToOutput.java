import java.util.*;

public class ActionToOutput {

    public static List<List<OutputRecord>> toOutput(List<Action> actionlist, List<Crane> cranes) {

        List<Action> actions = new ArrayList<>(actionlist);
        List<Map<Integer, Action>> actionFrames = new ArrayList<>();

        //bepaal welke acties in parallel gebeuren
        int index = 0;
        while (!actions.isEmpty()) {
            actionFrames.add(new HashMap<>());
            for (Crane crane : cranes) {
                for (int i = 0; i < actions.size(); i++) {
                    Action action = actions.get(i);
                    if (canExecute(crane, action) && !overlapWithOtherActions(action, actionFrames.get(index), actions, i)) {
                        actionFrames.get(index).put(crane.id, action);
                        actions.remove(action);
                        break;
                    }
                }
            }
            index++;
        }

        actionFrames.forEach(m -> {m.forEach((key, value)-> {
            System.out.print("crane " + key + " container " + value.container.id + " from x " + value.prevSlot.x + " to x " + value.slot.x + ", ");
        });
            System.out.println();});

        return toOutputRecords(actionFrames, cranes);
    }

    public static List<List<OutputRecord>> toOutputRecords(List<Map<Integer, Action>> actionlist, List<Crane> cranes){
        List<List<OutputRecord>> records = new ArrayList<>();
        List<Map<Integer, Action>> actions = new ArrayList<>(actionlist);
        double time = 0;
        int index = 0;

        for(Map<Integer, Action> map : actions){
            records.add(new ArrayList<>());
            double duration = 0;
            for(Map.Entry<Integer, Action> entry : map.entrySet()){
                //move crane to pickup duration
                double xdurationcrane = Math.abs(cranes.get(entry.getKey()).x - entry.getValue().prevSlot.x) / cranes.get(entry.getKey()).speedX;
                double ydurationcrane = Math.abs(cranes.get(entry.getKey()).y - entry.getValue().prevSlot.y) / cranes.get(entry.getKey()).speedY;
                double pickupduration = Math.max(xdurationcrane, ydurationcrane);

                //get move duration
                double xduration = Math.abs((double)entry.getValue().slot.x - entry.getValue().prevSlot.x) / cranes.get(entry.getKey()).speedX;
                double yduration = Math.abs((double)entry.getValue().slot.y - entry.getValue().prevSlot.y) / cranes.get(entry.getKey()).speedY;
                double moveduration = Math.max(xduration, yduration);

                if(pickupduration + moveduration > duration){
                    duration = pickupduration + moveduration;
                }

                //add record
                records.get(index).add(
                        new OutputRecord(
                                entry.getKey(),
                                entry.getValue().container.id,
                                time + pickupduration,
                                time + pickupduration + moveduration,
                                entry.getValue().prevSlot.x + (double)(entry.getValue().container.length)/2,
                                entry.getValue().prevSlot.y + 0.5,
                                entry.getValue().slot.x + (double)(entry.getValue().container.length)/2,
                                entry.getValue().slot.y + 0.5,
                                entry.getValue()
                                )
                );
            }

            //move cranes without task out of way
            for(Crane crane : cranes){
                if(!map.containsKey(crane.id)){
                    List<Integer> safezone = getSafeSpaces(crane, map, cranes).stream().toList();
                    int safe;
                    if(crane.id > cranes.size()/2){
                        safe = safezone.get(0);
                    }
                    else{
                        safe = safezone.get(safezone.size()-1);
                    }
                    records.get(index).add(
                            new OutputRecord(
                                    crane.id,
                                    -1,
                                    time + 1,
                                    time + 2,
                                    -1,   //TODO moet vorige ppos zijn
                                    -1,
                                    safe,
                                    -1,
                                    null
                            )
                    );
                }
            }

            time += duration;
            index++;
        }
        return records;
    }

    public  static Set<Integer> getSafeSpaces(Crane crane, Map<Integer, Action> actions, List<Crane> cranes){
        Set<Integer> range = new HashSet<>();
        for(int i = crane.xMin; i < crane.xMax; i++){
            range.add(i);
        }
        List<Crane> tasked = new ArrayList<>();
        actions.keySet().forEach(integer -> tasked.add(cranes.get(integer)));
        for(Crane tc : tasked){
            Set<Integer> newrange = new HashSet<>();
            for(int i = tc.xMin; i < tc.xMax; i++){
                newrange.add(i);
            }
            range.removeAll(newrange);
        }
        return range;
    }

    public static boolean overlapWithOtherActions(Action action, Map<Integer, Action> craneactions, List<Action> actions, int index) {

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
                System.out.println("overlap: " + leftmostslot + "," + rightmostslot + " binnen: " + leftmostslotc + "," + rightmostslotc + " lengte: " + a.container.length);
                craneactions.forEach((key, value)-> System.out.println(key + " " + value.container.id  + " tussen " + value.slot.x + ", " + value.prevSlot.x ));
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
            System.out.println("kan executen: " + action.container.id + " naar x " + action.container.slots.get(0).x);
            return true;
        }
        System.out.println("kan niet executen");
        System.out.println(leftmostslot + "," + rightmostslot + " niet binnen: " + crane.xMin + "," + crane.xMax + " length: " + action.container.length);
        return false;
    }

}
