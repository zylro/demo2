package zylro.atc.resource;

import java.util.UUID;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zylro.atc.Utils;
import zylro.atc.auth.ApiKeyAuth;
import zylro.atc.model.Aircraft;
import zylro.atc.service.AirTrafficControlService;

/**
 *
 * @author wot
 */
@ApiKeyAuth
@Path("/atc")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AirTrafficControlResource {

    private static final Logger LOG = LoggerFactory.getLogger(AirTrafficControlResource.class);
    private final AirTrafficControlService atcService;

    public AirTrafficControlResource(AirTrafficControlService atcService) {
        this.atcService = atcService;
    }

    @POST
    public Response enqueueAircraft(@Valid Aircraft aircraft) {
        LOG.info("enqueueAircraft request: " + Utils.toJson(aircraft));
        if (aircraft.getId() == null) {
            aircraft.setId(UUID.randomUUID());
        }
        atcService.enqueue(aircraft);
        return Response.ok().build();
    }

    @GET
    public Response dequeueAircraft() {
        LOG.info("dequeueAircraft request");
        return Response.ok(atcService.dequeue()).build();
    }

    @GET
    @Path("/queue")
    public Response getQueueState() {
        LOG.info("getQueueState request");
        return Response.ok(atcService.getQueueState()).build();
    }
}
