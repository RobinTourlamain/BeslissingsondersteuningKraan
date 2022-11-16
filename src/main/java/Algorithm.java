import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Algorithm {

    public static void execute(List<Container> required, List<List<Slot>> area, List<Container> containers){

        while(!required.isEmpty()){
            for(Container c: findExposed(area, required)){
                //haal c met kraan
                required.remove(c);
            }
        }
    }


    public static List<Container> findExposed(List<List<Slot>> area, List<Container> req){
        List<Container> res = new ArrayList<>();
        int[] count = new int[req.size()+1];

        //check in elke slot welke container vanboven ligt en hou ook bij in hoeveel slots deze container vanboven ligt
        for (Slot s: area.get(0)) {
            int cid = s.containers.peek().id;
            count[cid] += 1;
        }

        for(Container c : req){
            if(c.length == count[c.id]){
                res.add(c);
            }
        }

        return res;
    }
}
