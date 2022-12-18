import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Input input = new Input("instances/5t/TerminalB_20_10_3_2_160.json");
        //Input input = new Input("instances/example/example.json");
        //Input input = new Input("instances/2mh/MH2Terminal_20_10_3_2_100.json");

        Terminal startTerminal = input.getTerminal();
        System.out.println(startTerminal.cranes.size());

        List<Action> result = new ArrayList<>();

        if (startTerminal.targetHeight == 0) {
            Input inputTarget = new Input("instances/5t/targetTerminalB_20_10_3_2_160.json");

            Terminal endTerminal = inputTarget.getTerminal();

            List<Integer> changedContainerIds = Algorithm.findChangedContainerIds(startTerminal, endTerminal);

            for (Integer id : changedContainerIds) {
                if (startTerminal.containers.get(id).isMovable()) {
                    System.out.println(id + " movable");
                }
                else {
                    System.out.println(id + " not movable");
                }
            }

            for (int height = 0; height < startTerminal.maxHeight; height++) {
                for (int slotnumber = 0; slotnumber < startTerminal.slots.size(); slotnumber++) {
                    if (endTerminal.slots.get(slotnumber).containers.size() > height) {

                        Container container = startTerminal.containers.get(endTerminal.slots.get(slotnumber).containers.get(height).id);

                        if (startTerminal.slots.get(slotnumber).containers.size() > height) {
                            Container originalcontainer = startTerminal.slots.get(slotnumber).containers.get(height);
                            if (container.id == originalcontainer.id) {
                                continue;
                            }
                        }

                        result.addAll(Recursion.makeSolution(startTerminal, startTerminal.slots.get(slotnumber), height, container));


//                        //check slot want in target staat hier een container
//                        if (!(startTerminal.slots.get(slotnumber).containers.size() <= height)) {
//                            //er staat een container
//                            Container originalcontainer = startTerminal.slots.get(slotnumber).containers.get(height);
//                            if (container.id != originalcontainer.id) {
//                                //niet zelfde container, moet wijzigen
//                                Algorithm.clearThisSlotAndLengthOfContainer(startTerminal, slotnumber, height, container);
//                                //maak container vrij
//                                Algorithm.prepareContainerMove(startTerminal, container);
//                                //verplaats
//                                Algorithm.moveContainerToTarget(startTerminal, container, endTerminal.slots.get(slotnumber).containers.get(height).slots);
//                            }
//                        }
//                        else {
//                            //er staat geen container, moet wijzigen
//                            Algorithm.clearThisSlotAndLengthOfContainer(startTerminal, slotnumber, height, container);
//                            //maak container vrij
//                            Algorithm.prepareContainerMove(startTerminal, container);
//                            //verplaats
//                            Algorithm.moveContainerToTarget(startTerminal, container, endTerminal.slots.get(slotnumber).containers.get(height).slots);
//                        }
                    }
                }
            }

            boolean mistakeFound = false;
            for (int i = 0; i < endTerminal.slots.size(); i++) {
                Slot startSlot = startTerminal.slots.get(i);
                Slot endSlot = endTerminal.slots.get(i);

                for (int j = 0; j < endSlot.containers.size(); j++) {
                    if (startSlot.containers.get(j).id != endSlot.containers.get(j).id) {
                        System.out.println("fout gevonden: " + startSlot.id);
                        mistakeFound = true;
                    }
                }
            }
            if (!mistakeFound) System.out.println("all correct");

        }
        else {
            Algorithm.findContainerIdsAboveMaxHeight(startTerminal).forEach(System.out::println);
        }

        result.forEach(System.out::println);
    }

//    public static boolean recursiveMain(){
//
//        for (int height = 0; height < startTerminal.maxHeight; height++) {
//            for (int slotnumber = 0; slotnumber < startTerminal.slots.size(); slotnumber++) {
//                if (!(endTerminal.slots.get(slotnumber).containers.size() <= height)) {
//                    Container container = startTerminal.containers.get(endTerminal.slots.get(slotnumber).containers.get(height).id);
//
//                    //check slot want in target staat hier een container
//                    if (!(startTerminal.slots.get(slotnumber).containers.size() <= height)) {
//                        //er staat een container
//                        Container originalcontainer = startTerminal.slots.get(slotnumber).containers.get(height);
//                        if (container.id != originalcontainer.id) {
//                            //niet zelfde container, moet wijzigen
//                            Algorithm.clearThisSlotAndLengthOfContainer(startTerminal, slotnumber, height, container);
//                            //maak container vrij
//                            Algorithm.prepareContainerMove(startTerminal, container);
//                            //verplaats
//                            Algorithm.moveContainerToTarget(startTerminal, container, endTerminal.slots.get(slotnumber).containers.get(height).slots);
//                        }
//                    }
//                    else {
//                        //er staat geen container, moet wijzigen
//                        Algorithm.clearThisSlotAndLengthOfContainer(startTerminal, slotnumber, height, container);
//                        //maak container vrij
//                        Algorithm.prepareContainerMove(startTerminal, container);
//                        //verplaats
//                        Algorithm.moveContainerToTarget(startTerminal, container, endTerminal.slots.get(slotnumber).containers.get(height).slots);
//                    }
//                }
//            }
//        }
//
//        return true;
//    }
}
