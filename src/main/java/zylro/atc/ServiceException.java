package zylro.atc;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import zylro.atc.model.ErrorMessage;

/**
 * Wrapper to return different status codes easier in a Response object
 *
 * @author wot
 */
public class ServiceException extends WebApplicationException {

    public ServiceException(int status, String message) {
        super(Response.status(status).entity(new ErrorMessage(message)).build());
    }
}
