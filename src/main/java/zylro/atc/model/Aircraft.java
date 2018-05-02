package zylro.atc.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.bson.Document;
import static zylro.atc.Utils.fromJson;

/**
 * POJO representing an aircraft
 *
 * @author wot
 */
public class Aircraft implements Comparable<Aircraft> {

    protected final Map<String, Object> additionalProperties = new HashMap<>();

    @Override
    public int compareTo(Aircraft a) {
        return 0;
    }

    public enum Type {
        Emergency, VIP, Passenger, Cargo
    }

    public enum Size {
        Small, Large
    }
    //mongodb uses this as the identifying field
    public static String ID_FIELD = "_id";
    private UUID id;
    @NotNull
    private Type type;
    @NotNull
    private Size size;

    public static Aircraft fromDoc(Document doc) {
        return fromJson(doc.toJson(), Aircraft.class);
    }

    public Document toDoc() {
        return new Document(ID_FIELD, id)
                .append("size", size.toString())
                .append("type", type.toString());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String key, Object value) {
        this.additionalProperties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
}
