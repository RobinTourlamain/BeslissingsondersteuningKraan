public class Main {
    public static void main(String[] args) {

        Input input = new Input("instances/1t/TerminalA_20_10_3_2_100.json");

        Terminal terminal = input.getTerminal();

        Algorithm.findExposed(terminal.area).forEach(c -> {
            System.out.println(c.id);
        });


        if (terminal.targetHeight == 0) {
            Input inputTarget = new Input("instances/1t/targetTerminalA_20_10_3_2_100.json");

            Terminal targetTerminal = inputTarget.getTerminal();

        }


    }
}
