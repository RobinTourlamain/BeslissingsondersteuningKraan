import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Action> main() {

        //Input input = new Input("instances/1t/TerminalA_20_10_3_2_100.json");
        //Input input = new Input("instances/3t/TerminalA_20_10_3_2_160.json");
        Input input = new Input("instances/5t/TerminalB_20_10_3_2_160.json");
        //Input input = new Input("instances/6t/Terminal_10_10_3_1_100.json");
        //Input input = new Input("instances/2mh/MH2Terminal_20_10_3_2_100.json");
        //Input input = new Input("instances/4mh/MH2Terminal_20_10_3_2_160.json");

        Terminal startTerminal = input.getTerminal();
        System.out.println(startTerminal.cranes.size());

        List<Action> result = new ArrayList<>();

        if (startTerminal.targetHeight == 0) {
            //Input inputTarget = new Input("instances/1t/targetTerminalA_20_10_3_2_100.json");
            //Input inputTarget = new Input("instances/3t/targetTerminalA_20_10_3_2_160.json");
            Input inputTarget = new Input("instances/5t/targetTerminalB_20_10_3_2_160.json");
            //Input inputTarget = new Input("instances/6t/targetTerminal_10_10_3_1_100.json");

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
                    }
                }

                for (int i = 0; i < startTerminal.slots.size(); i++) {
                    Slot startSlot = startTerminal.slots.get(i);
                    Slot endSlot = endTerminal.slots.get(i);
                    if (endSlot.containers.size() > height) {
                        if (startSlot.containers.size() <= height) {
                            height -= 1;
                            break;
                        }
                        if (startSlot.containers.get(height).id != endSlot.containers.get(height).id) {
                            height -= 1;
                            break;
                        }
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
            if (!mistakeFound) {
                System.out.println("all correct");
                return result;
            }
        }
        else {
            System.out.println(Algorithm.findContainersAboveMaxHeight(startTerminal).size());
            result.addAll(HeightReduction.makeSolution(startTerminal));
            result.forEach(action -> {
                System.out.println("container " + action.container.id + " naar slot " + action.slot.id);
            });
            return result;
        }

        result.forEach(System.out::println);
        return result;
    }
}
