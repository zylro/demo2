package zylro.atc.dataaccess;

import com.mongodb.MongoClient;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.LinkedList;
import java.util.UUID;
import org.bson.Document;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zylro.atc.ServiceException;
import static zylro.atc.dataaccess.BaseDataAccess.UPSERT;
import zylro.atc.model.AirTrafficPriorityQueue;
import zylro.atc.model.Aircraft;

/**
 * Data access for air traffic control queues
 *
 * @author wot
 */
public class AirTrafficQueueDataAccess extends BaseDataAccess {

    private static final Logger LOG = LoggerFactory.getLogger(AirTrafficQueueDataAccess.class);
    private static final UUID QUEUE_ID = new UUID(0, 0);

    public AirTrafficQueueDataAccess(MongoClient mongoClient) {
        super(mongoClient);
        LOG.info("AirTrafficQueueDataAccess initialized");
    }

    @Override
    protected String getCollectionName() {
        return "queue";
    }

    public AirTrafficPriorityQueue getPriorityQueue() {
        Document doc = data.find(eq(AirTrafficPriorityQueue.ID_FIELD, QUEUE_ID)).first();
        if (doc == null) {
            LOG.debug("Queue not found, creating a new one.");
            upsertQueue(new AirTrafficPriorityQueue());
            doc = data.find(eq(AirTrafficPriorityQueue.ID_FIELD, QUEUE_ID)).first();
        }
        return AirTrafficPriorityQueue.fromDoc(doc);
    }

    public void upsertQueue(AirTrafficPriorityQueue queue) {
        UpdateResult result = data.replaceOne(eq(AirTrafficPriorityQueue.ID_FIELD,
                QUEUE_ID), queue.toDoc(QUEUE_ID), UPSERT);
        boolean updateSuccessful = result.isModifiedCountAvailable()
                && result.getMatchedCount() == 1L;
        boolean insertSuccessful = result.getUpsertedId() != null;
        if (!updateSuccessful && !insertSuccessful) {
            LOG.error("Upsert failed: insert {}; update {}", insertSuccessful, updateSuccessful);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR_500, "Upsert failed");
        }
    }

    public void deleteAircraft(Aircraft aircraft) {
        DeleteResult result = data.deleteOne(eq(Aircraft.ID_FIELD, aircraft.getId()));
        if (result.getDeletedCount() != 1L) {
            LOG.error("Delete failed with count; ", result.getDeletedCount());
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR_500, "Delete failed");
        }
    }
}
