package zylro.atc.auth;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zylro.atc.ServiceException;

/**
 * Filter to look for api key
 *
 * @author wot
 */
@ApiKeyAuth
public class ApiKeyFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyFilter.class);
    private static final String API_KEY = "api_key ";
    private final String apiKey;

    public ApiKeyFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        final String requestApiKey = getApiKey(context);
        LOG.debug("Request with apiKey {}", requestApiKey);
        if (!apiKey.equals(requestApiKey)) {
            throw new ServiceException(HttpStatus.FORBIDDEN_403, "Invalid api_key");
        }
    }

    private String getApiKey(ContainerRequestContext context) {
        MultivaluedMap<String, String> headers = context.getHeaders();
        if (headers == null || headers.isEmpty()
                || !headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED_401, "Authorization header missing");
        }
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isEmpty()
                || !authHeader.toLowerCase().startsWith(API_KEY)) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED_401, "Invalid Authorization header");
        }
        authHeader = authHeader.toLowerCase();
        return authHeader.substring(authHeader.indexOf(API_KEY) + API_KEY.length());
    }

}
