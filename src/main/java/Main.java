import java.util.List;

public class Main {
    public static void main(String[] args) {

        Input input = new Input("terminal_4_3.json");

        List<Container> containers = input.getContainers();
        List<Slot> slots = input.getSlots();
        List<List<Slot>> area = input.getArea();



        containers.forEach(c-> c.slots.forEach(s -> System.out.println(s.id)));

        //new GUI();


        Algorithm.findExposed(area).forEach(c -> {
            System.out.println(c.id);
        });

        Crane crane1 = new Crane(area, 1,1);
        Crane crane2 = new Crane(area,1,1);
        crane1.setLocation(area.get(0).get(0));
        crane2.setLocation(area.get(area.size()-1).get(0));
        List<Crane> cranes = new ArrayList<>();
        cranes.add(crane1);
        //cranes.add(crane2);                                          //veroorzaakt deadlock want staat in de weg
        Depot depot = new Depot(area, cranes,1);

        depot.addMove(4,4,crane1);

        depot.calcMovements();

        crane1.printPath();



    }
}
