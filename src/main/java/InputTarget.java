import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InputTarget {

    private final List<Assignment> assignments;

    public InputTarget(String fileName){
        FileReader fileReader;
        JsonObject jsonObject;
        try {
            fileReader = new FileReader(fileName);
            jsonObject = (JsonObject) Jsoner.deserialize(fileReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String terminalName = (String) jsonObject.get("name");
        int terminalmaxheight = ((BigDecimal) jsonObject.get("maxheight")).intValue();

        this.assignments = readAssignments(jsonObject);
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public static List<Assignment> readAssignments(JsonObject jsonObject) {
        List<Assignment> assignments = new ArrayList<>();
        JsonArray jsAssignments = (JsonArray) jsonObject.get("assignments");
        for (Object jsAssignment : jsAssignments) {
            JsonObject assignment = (JsonObject) jsAssignment;
            assignments.add(new Assignment(((BigDecimal) assignment.get("slot_id")).intValue(), ((BigDecimal) assignment.get("container_id")).intValue()));
        }
        return assignments;
    }
}
