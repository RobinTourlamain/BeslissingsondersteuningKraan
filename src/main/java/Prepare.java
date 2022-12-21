import java.util.ArrayList;
import java.util.List;

public class Prepare {
    public static List<Action> prepare(String filename) {

        Input input = new Input(filename);
        Terminal startTerminal = input.getTerminal();

        List<Action> result = new ArrayList<>();

        if (startTerminal.targetHeight == 0) {
            String[] filenameSplit = filename.split("/");
            String targetFilename = filenameSplit[0] + "/" + filenameSplit[1] + "/" + "target" + filenameSplit[2];

            Input inputTarget = new Input(targetFilename);
            Terminal endTerminal = inputTarget.getTerminal();

            for (int height = 0; height < startTerminal.maxHeight; height++) {
                for (int slotNumber = 0; slotNumber < startTerminal.slots.size(); slotNumber++) {
                    if (endTerminal.slots.get(slotNumber).containers.size() > height) {

                        Container container = startTerminal.containers.get(endTerminal.slots.get(slotNumber).containers.get(height).id);

                        if (startTerminal.slots.get(slotNumber).containers.size() > height) {
                            Container originalcontainer = startTerminal.slots.get(slotNumber).containers.get(height);
                            if (container.id == originalcontainer.id) {
                                continue;
                            }
                        }

                        result.addAll(Transfer.makeSolution(startTerminal, startTerminal.slots.get(slotNumber), height, container));
                    }
                }

                //check if level correct, if not stay on same level
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

            //algorithm finished, check for mistakes
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
                //result.forEach(action -> System.out.println("container " + action.container.id + " naar x " + action.slot.x));
                return result;
            }
        }
        else {
            System.out.println(Algorithm.findContainersAboveMaxHeight(startTerminal).size());
            result.addAll(HeightReduction.makeSolution(startTerminal));
            result.forEach(action -> System.out.println("container " + action.container.id + " naar x " + action.slot.x));

            boolean mistakeFound = false;
            for (Slot slot : startTerminal.slots) {
                if (slot.containers.size() > startTerminal.targetHeight) {
                    System.out.println("fout gevonden");
                    mistakeFound = true;
                    break;
                }
            }
            if (!mistakeFound) {
                System.out.println("all correct");
            }

            return result;
        }

        //result.forEach(System.out::println);
        return result;
    }
}
