import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Input input = new Input("instances/1t/TerminalA_20_10_3_2_100.json");
        Input input = new Input("instances/2mh/MH2Terminal_20_10_3_2_100.json");

        Terminal startTerminal = input.getTerminal();


        if (startTerminal.targetHeight == 0) {
            Input inputTarget = new Input("instances/1t/targetTerminalA_20_10_3_2_100.json");

            Terminal endTerminal = inputTarget.getTerminal();

            List<Integer> changedContainerIds = Algorithm.findChangedContainerIds(startTerminal, endTerminal);

            for (int i = 0; i < startTerminal.maxHeight; i++) {
                for (int j = 0; j < startTerminal.slots.size(); j++) {
                    if (!(endTerminal.slots.get(j).containers.size() <= i)) {
                        Container container = startTerminal.containers.get(endTerminal.slots.get(j).containers.get(i).id);

                        //check want in target staat hier een container
                        if (startTerminal.slots.get(j).containers.size() <= i) {
                            //maak lengte vrij
                            //maak container vrij
                            //verplaats

                        }
                        else {
                            //maak deze slot eerst vrij
                            //maak lengte vrij
                            //maak container vrij
                            //verplaats
                        }
                    }
                }
            }


        }
        else {

            Algorithm.findContainerIdsAboveMaxHeight(startTerminal).forEach(System.out::println);

        }

    }
}
