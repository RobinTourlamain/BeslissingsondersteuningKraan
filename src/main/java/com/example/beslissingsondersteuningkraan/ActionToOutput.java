package com.example.beslissingsondersteuningkraan;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ActionToOutput {

    public static List<List<OutputRecord>> toOutput(List<Action> actionlist, List<Crane> cranes, String pathToOutput) {

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

//        actionFrames.forEach(m -> {m.forEach((key, value)-> {
//            System.out.print("crane " + key + " container " + value.container.id + " from x " + value.prevSlot.x + " to x " + value.slot.x + ", ");
//        });
//            System.out.println();});

        List<List<OutputRecord>> recordMatrix = toOutputRecords(actionFrames, cranes);

        try (FileWriter fileWriter = new FileWriter(Objects.requireNonNullElse(pathToOutput, "output.txt"))) {
            fileWriter.write("%CraneId;ContainerId;PickupTime;EndTime;PickupPosX;PickupPosY;EndPosX;EndPosY;");
            fileWriter.write(System.lineSeparator());

            for (List<OutputRecord> recordList : recordMatrix) {
                recordList.sort(Comparator.comparing(outputRecord -> outputRecord.craneId));
                for (OutputRecord record : recordList) {
                    fileWriter.write(record.toString());
                    fileWriter.write(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return recordMatrix;
    }

    public static List<List<OutputRecord>> toOutputRecords(List<Map<Integer, Action>> actionlist, List<Crane> cranes) {
        List<List<OutputRecord>> records = new ArrayList<>();
        List<Map<Integer, Action>> actions = new ArrayList<>(actionlist);
        double time = 0;
        int index = 0;

        for (Map<Integer, Action> map : actions) {
            records.add(new ArrayList<>());
            double duration = 0;
            for (Map.Entry<Integer, Action> entry : map.entrySet()) {
                //move crane to pickup duration
                double xDurationCrane = Math.abs(cranes.get(entry.getKey()).x - entry.getValue().prevSlot.x) / cranes.get(entry.getKey()).speedX;
                double yDurationCrane = Math.abs(cranes.get(entry.getKey()).y - entry.getValue().prevSlot.y) / cranes.get(entry.getKey()).speedY;
                double pickupDuration = Math.max(xDurationCrane, yDurationCrane);

                //get move duration
                double xDuration = Math.abs((double)entry.getValue().slot.x - entry.getValue().prevSlot.x) / cranes.get(entry.getKey()).speedX;
                double yDuration = Math.abs((double)entry.getValue().slot.y - entry.getValue().prevSlot.y) / cranes.get(entry.getKey()).speedY;
                double moveDuration = Math.max(xDuration, yDuration);

                if (pickupDuration + moveDuration > duration) {
                    duration = pickupDuration + moveDuration;
                }

                //add record
                records.get(index).add(
                        new OutputRecord(
                                entry.getKey(),
                                entry.getValue().container.id,
                                time + pickupDuration,
                                time + pickupDuration + moveDuration,
                                entry.getValue().prevSlot.x +  ((double) entry.getValue().container.length / 2),
                                entry.getValue().prevSlot.y + 0.5,
                                entry.getValue().slot.x +  ((double) entry.getValue().container.length / 2),
                                entry.getValue().slot.y + 0.5,
                                entry.getValue()
                        )
                );

                cranes.get(entry.getKey()).x = entry.getValue().slot.x;
                cranes.get(entry.getKey()).y = entry.getValue().slot.y;

            }

            //move cranes without task out of way
            for (Crane crane : cranes) {
                if (!map.containsKey(crane.id)) {
                    List<Integer> safezone = getSafeSpaces(crane, map, cranes).stream().toList();
                    int safe;
                    if (crane.id > cranes.size()/2) {
                        safe = safezone.get(0);
                    }
                    else {
                        safe = safezone.get(safezone.size() - 1);
                    }



                    double lastEndPosX = crane.x;
                    double lastEndPosY = crane.y;

                    boolean prevFound = false;
                    int recordIndex = records.size() - 2;
                    while (!prevFound && records.size() > 2) {
                        for (OutputRecord outputRecord : records.get(recordIndex)) {
                            if (outputRecord.craneId == crane.id) {
                                lastEndPosX = outputRecord.endPosX;
                                lastEndPosY = outputRecord.endPosY;
                                prevFound = true;
                                break;
                            }
                        }
                        recordIndex--;
                    }

                    if (safe == lastEndPosX) {
                        continue;
                    }

                    double moveAwayDuration = Math.abs(safe - lastEndPosX) / crane.speedX;

                    records.get(index).add(
                            new OutputRecord(
                                    crane.id,
                                    -1,
                                    time + 1,
                                    time + moveAwayDuration,
                                    lastEndPosX,
                                    lastEndPosY,
                                    safe,
                                    lastEndPosY,
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

    public static Set<Integer> getSafeSpaces(Crane crane, Map<Integer, Action> actions, List<Crane> cranes) {
        Set<Integer> range = new HashSet<>();
        for (int i = crane.xMin; i < crane.xMax; i++) {
            range.add(i);
        }
        List<Crane> tasked = new ArrayList<>();
        actions.keySet().forEach(integer -> tasked.add(cranes.get(integer)));
        for (Crane tc : tasked) {
            Set<Integer> newrange = new HashSet<>();
            for (int i = tc.xMin; i < tc.xMax; i++) {
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
        int leftMostSlot = Collections.min(slots);
        int rightMostSlot = Collections.max(slots);

        for (Action a : consider) {
            List<Integer> compareSlots = new ArrayList<>();
            compareSlots.add(a.slot.x);
            compareSlots.add(a.slot.x + a.container.length - 1);
            compareSlots.add(a.prevSlot.x);
            compareSlots.add(a.prevSlot.x + a.container.length - 1);
            int leftMostSlotCompare = Collections.min(compareSlots);
            int rightMostSlotCompare = Collections.max(compareSlots);

            if (leftMostSlot <= rightMostSlotCompare && rightMostSlot >= leftMostSlotCompare) {
                //System.out.println("overlap: " + leftMostSlot + "," + rightMostSlot + " binnen: " + leftMostSlotCompare + "," + rightMostSlotCompare + " lengte: " + a.container.length);
                //craneactions.forEach((key, value)-> System.out.println(key + " " + value.container.id  + " tussen " + value.slot.x + ", " + value.prevSlot.x ));
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

        int leftMostSlot = Collections.min(slots);
        int rightMostSlot = Collections.max(slots);

        if (action.container.length > 1) {
            leftMostSlot++;
            rightMostSlot--;
        }

        if (crane.xMin <= leftMostSlot && rightMostSlot <= crane.xMax) {
            //System.out.println("kan executen: " + action.container.id + " naar x " + action.container.slots.get(0).x);
            return true;
        }
        //System.out.println("kan niet executen");
        //System.out.println(leftMostSlot + "," + rightMostSlot + " niet binnen: " + crane.xMin + "," + crane.xMax + " length: " + action.container.length);
        return false;
    }

}
