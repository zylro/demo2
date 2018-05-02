package zylro.atc.model;

import java.util.LinkedList;
import java.util.UUID;
import org.bson.Document;
import static zylro.atc.Utils.fromJson;

/**
 *
 * @author wot
 */
public class AirTrafficPriorityQueue {

    private LinkedList<UUID> queue;
    //mongodb uses this as the identifying field
    public static String ID_FIELD = "_id";

    public static AirTrafficPriorityQueue fromDoc(Document doc) {
        return fromJson(doc.toJson(), AirTrafficPriorityQueue.class);
    }

    public Document toDoc(UUID queueId) {
        return new Document(ID_FIELD, queueId)
                .append("list", queue);
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
