package zylro.atc.model;

import java.util.LinkedList;
import java.util.UUID;
import org.bson.Document;
import zylro.atc.Utils;
import static zylro.atc.Utils.fromJson;

/**
 * Wrapper class to store the priority queue in mongo
 *
 * @author wot
 */
public class AirTrafficPriorityQueue extends DbObject {

    private LinkedList<UUID> queue;

    public static AirTrafficPriorityQueue fromDoc(Document doc) {
        UUID id = doc.get(ID_FIELD, UUID.class);
        doc.remove(ID_FIELD);
        doc.put("id", id);
        return fromJson(Utils.toJson(doc), AirTrafficPriorityQueue.class);
    }

    public Document toDoc(UUID queueId) {
        return new Document(ID_FIELD, queueId)
                .append("queue", queue);
    }

    public AirTrafficPriorityQueue() {
        this.queue = new LinkedList<>();
    }

    public LinkedList<UUID> getQueue() {
        return queue;
    }

    public void setQueue(LinkedList<UUID> queue) {
        this.queue = queue;
    }

}
