import java.util.List;

public class Main {
    public static void main(String[] args) {

        Input input = new Input("instances/3t/TerminalA_20_10_3_2_160.json");
        //Input input = new Input("instances/example/example.json");
        //Input input = new Input("instances/2mh/MH2Terminal_20_10_3_2_100.json");

        Terminal startTerminal = input.getTerminal();
        System.out.println(startTerminal.cranes.size());


        if (startTerminal.targetHeight == 0) {
            Input inputTarget = new Input("instances/3t/targetTerminalA_20_10_3_2_160.json");

            Terminal endTerminal = inputTarget.getTerminal();

            List<Integer> changedContainerIds = Algorithm.findChangedContainerIds(startTerminal, endTerminal);

            for (int height = 0; height < startTerminal.maxHeight; height++) {
                for (int slotnumber = 0; slotnumber < startTerminal.slots.size(); slotnumber++) {
                    if (!(endTerminal.slots.get(slotnumber).containers.size() <= height)) {
                        Container container = startTerminal.containers.get(endTerminal.slots.get(slotnumber).containers.get(height).id);

                        //check slot want in target staat hier een container
                        if (!(startTerminal.slots.get(slotnumber).containers.size() <= height)) {
                            //er staat een container
                            Container originalcontainer = startTerminal.slots.get(slotnumber).containers.get(height);
                            if (container.id != originalcontainer.id) {
                                //niet zelfde container, moet wijzigen
                                Algorithm.clearThisSlotAndLengthOfContainer(startTerminal, slotnumber, height, container);
                                //maak container vrij
                                Algorithm.prepareContainerMove(startTerminal, container);
                                //verplaats
                                Algorithm.moveContainerToTarget(startTerminal, container, endTerminal.slots.get(slotnumber).containers.get(height).slots);
                            }
                        }
                        else {
                            //er staat geen container, moet wijzigen
                            Algorithm.clearThisSlotAndLengthOfContainer(startTerminal, slotnumber, height, container);
                            //maak container vrij
                            Algorithm.prepareContainerMove(startTerminal, container);
                            //verplaats
                            Algorithm.moveContainerToTarget(startTerminal, container, endTerminal.slots.get(slotnumber).containers.get(height).slots);
                        }
                    }
                }
            }
        }
        else {
            Algorithm.findContainerIdsAboveMaxHeight(startTerminal).forEach(System.out::println);
        }
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
