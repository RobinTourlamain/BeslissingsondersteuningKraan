import java.util.List;

public class Main {
    public static void main(String[] args) {

        InputBegin inputBegin = new InputBegin("terminal22_1_100_1_10.json");

        List<Container> containers = inputBegin.getContainers();
        List<Slot> slots = inputBegin.getSlots();
        List<List<Slot>> area = inputBegin.getArea();

        InputTarget inputTarget = new InputTarget("terminal22_1_100_1_10target.json");
        List<Assignment> assignments = inputTarget.getAssignments();

        assignments.forEach(assignment -> System.out.println(assignment.slotId + " " + assignment.containerId));

        //new GUI();


        Algorithm.findExposed(area).forEach(c -> {
            System.out.println(c.id);
        });


//        Crane crane1 = new Crane(area, 1,1);
//        Crane crane2 = new Crane(area,1,1);
//        crane1.setLocation(area.get(0).get(0));
//        crane2.setLocation(area.get(area.size()-1).get(0));
//        List<Crane> cranes = new ArrayList<>();
//        cranes.add(crane1);
//        cranes.add(crane2);                                          //veroorzaakt deadlock want staat in de weg
//        Terminal depot = new Terminal(area, cranes,1);
//
//        depot.addMove(4,4,crane1);
//
//        depot.calcMovements();
//
//        crane1.printPath();

    }
}
