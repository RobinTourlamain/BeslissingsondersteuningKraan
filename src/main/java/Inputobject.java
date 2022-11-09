import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsonable;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public class Inputobject implements Jsonable {
    public String name;
    public JsonArray containers;
    public JsonArray slots;
    public JsonArray assignments;

    @Override
    public String toString(){
        return name;
    }
    @Override
    public String toJson() {
        final StringWriter writable = new StringWriter();
        try {
            this.toJson(writable);
        } catch (final IOException e) {
        }
        return writable.toString();
    }

    @Override
    public void toJson(Writer writer) throws IOException {
        final JsonObject json = new JsonObject();
        json.put("containers", this.containers);
        json.put("slots", this.slots);
        json.put("assignments", this.assignments);
        json.put("name", this.name);
        json.toJson(writer);
    }
}
