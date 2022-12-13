public class Main {
    public static void main(String[] args) {

        //Input input = new Input("instances/1t/TerminalA_20_10_3_2_100.json");
        Input input = new Input("instances/2mh/MH2Terminal_20_10_3_2_100.json");

        Terminal terminal = input.getTerminal();


        if (terminal.targetHeight == 0) {
            Input inputTarget = new Input("instances/1t/targetTerminalA_20_10_3_2_100.json");

            Terminal targetTerminal = inputTarget.getTerminal();

            Algorithm.findChangedContainerIds(terminal, targetTerminal).forEach(System.out::println);

        }
        else {

            Algorithm.findContainerIdsAboveMaxHeight(terminal).forEach(System.out::println);

        }


    }
}
