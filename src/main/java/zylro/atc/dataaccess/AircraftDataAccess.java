package zylro.atc.dataaccess;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.UUID;
import org.bson.Document;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zylro.atc.ServiceException;
import zylro.atc.model.Aircraft;

/**
 * Data access for aircrafts using mongodb
 *
 * @author wot
 */
public class AircraftDataAccess extends BaseDataAccess {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftDataAccess.class);

    public AircraftDataAccess(MongoClient mongoClient) {
        super(mongoClient);
        LOG.info("AircraftDataAccess initialized");
    }

    @Override
    protected String getCollectionName() {
        return "aircraft";
    }

    public Aircraft getAircraftById(UUID id) {
        Document doc = data.find(eq(Aircraft.ID_FIELD, id)).first();
        if (doc == null) {
            return null;
        }
        LOG.debug("Retrieved an aircraft with id: " + id);
        return Aircraft.fromDoc(doc);
    }

    public void upsertAircraft(Aircraft aircraft) {
        UpdateResult result = data.replaceOne(eq(Aircraft.ID_FIELD,
                aircraft.getId()), aircraft.toDoc(), UPSERT);
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
