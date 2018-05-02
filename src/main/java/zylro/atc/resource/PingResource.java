package zylro.atc.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * For pinging and health checks
 *
 * @author wot
 */
@Path("/ping")
public class PingResource {

    @GET
    public Response ping() {
        return Response.ok().build();
    }
}
