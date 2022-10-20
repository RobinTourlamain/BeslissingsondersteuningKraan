import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Coordinaat[][][] c = new Coordinaat[5][5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 5; k++) {
                    c[i][j][k] = new Coordinaat(i, j, k);
                }
            }
        }
        Crane crane1 = new Crane(c, 1,1);
        Crane crane2 = new Crane(c,1,1);
        crane1.setLocation(new Coordinaat(0,0,0));
        crane2.setLocation(new Coordinaat(2,2,0));
        List<Crane> cranes = new ArrayList<>();
        cranes.add(crane1);
        //cranes.add(crane2);                                          //veroorzaakt deadlock want staat in de weg
        Depot depot = new Depot(5,5,5, cranes,1);

        depot.addMove(4,4,crane1);

        depot.calcMovements();

        crane1.printPath();

    }
}
