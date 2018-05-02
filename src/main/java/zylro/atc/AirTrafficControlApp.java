package zylro.atc;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zylro.atc.auth.ApiKeyFilter;
import zylro.atc.dataaccess.AirTrafficQueueDataAccess;
import zylro.atc.dataaccess.AircraftDataAccess;
import zylro.atc.resource.AirTrafficControlResource;
import zylro.atc.resource.PingResource;
import zylro.atc.service.AirTrafficControlService;

/**
 * main service startup app for air traffic control
 *
 * @author wot
 */
public class AirTrafficControlApp extends Application<AtcConfig> {

    private static final Logger LOG = LoggerFactory.getLogger(AirTrafficControlApp.class);

    public static void main(String[] args) throws Exception {
        new AirTrafficControlApp().run(args);
    }

    public static String getMongoDBName() {
        return "atc";
    }

    @Override
    public void run(AtcConfig config, Environment env) throws Exception {
        MongoClient mongoClient = createMongoClient();
        AircraftDataAccess aircraftDA = new AircraftDataAccess(mongoClient);
        AirTrafficQueueDataAccess queueDA = new AirTrafficQueueDataAccess(mongoClient);

        AirTrafficControlService atcService = new AirTrafficControlService(aircraftDA, queueDA);
        env.healthChecks().register("aircraft-db", new DbHealthCheck(aircraftDA));
        env.healthChecks().register("queue-db", new DbHealthCheck(queueDA));
        env.jersey().register(new AirTrafficControlResource(atcService));
        env.jersey().register(new PingResource());
        env.jersey().register(new ApiKeyFilter(config.getApiKey()));
        LOG.info("AirTrafficControlApp initialized.");
    }

    private MongoClient createMongoClient() {
        try {
            MongoClientURI uri = new MongoClientURI(System.getenv("CONNECTION_STRING"));
            return new MongoClient(uri);
        } catch (Exception ex) {
            throw new Error("Failed to connect to DB", ex);
        }
    }
}
