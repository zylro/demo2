package zylro.atc;

import com.codahale.metrics.health.HealthCheck;
import zylro.atc.dataaccess.BaseDataAccess;

/**
 * Simple Db Health Check
 * @author wot
 */
public class DbHealthCheck extends HealthCheck{

    private final BaseDataAccess db;
    public DbHealthCheck(BaseDataAccess db) {
        this.db = db;
    }
    
    @Override
    protected Result check() throws Exception {
        if (db.ping()) {
            return HealthCheck.Result.healthy();
        }
        return HealthCheck.Result.unhealthy("Failed to ping mongo :(");
    }
    
}
