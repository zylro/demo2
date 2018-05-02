package zylro.atc.dataaccess;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zylro.atc.AirTrafficControlApp;

/**
 * base data access
 *
 * @author wot
 */
public abstract class BaseDataAccess {

    private static final Logger LOG = LoggerFactory.getLogger(BaseDataAccess.class);
    protected final MongoDatabase mdb;
    protected final MongoCollection<Document> data;
    protected static final UpdateOptions UPSERT = new UpdateOptions().upsert(true);

    public BaseDataAccess(MongoClient client) {
        try {
            mdb = client.getDatabase(AirTrafficControlApp.getMongoDBName());
            data = mdb.getCollection(getCollectionName());
        } catch (Exception ex) {
            throw new Error("Failed to connect to DB", ex);
        }
    }

    protected abstract String getCollectionName();

    public boolean ping() {
        try {
            mdb.runCommand(new Document("ping", 1));
        } catch (Exception ex) {
            LOG.error("Failed to ping DB.", ex);
            return false;
        }
        LOG.info("ping successful");
        return true;
    }
}
