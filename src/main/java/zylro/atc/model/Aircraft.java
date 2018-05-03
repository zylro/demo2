package zylro.atc.model;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.bson.Document;
import zylro.atc.Utils;
import static zylro.atc.Utils.fromJson;

/**
 * POJO representing an aircraft
 *
 * @author wot
 */
public class Aircraft extends DbObject implements Comparable<Aircraft> {

    /**
     * Compares 2 aircrafts by type and size
     *
     * @param a aicraft to compare to
     * @return 0 if they have same priority, >0 if this has higher priority {a}
     * and <0 if this has lower priority then {a}
     */
    @Override
    public int compareTo(Aircraft a) {
        if (this.type == a.type) {
            if (this.size == a.size) {
                return 0;
            }
            return this.size.ordinal() - a.size.ordinal();
        }
        return this.type.ordinal() - a.type.ordinal();
    }

    public enum Type {
        Cargo, Passenger, VIP, Emergency
    }

    public enum Size {
        Small, Large
    }

    private UUID id;
    @NotNull
    private Type type;
    @NotNull
    private Size size;

    public static Aircraft fromDoc(Document doc) {
        UUID id = doc.get(ID_FIELD, UUID.class);
        doc.remove(ID_FIELD);
        doc.put("id", id);
        return fromJson(Utils.toJson(doc), Aircraft.class);
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

}
