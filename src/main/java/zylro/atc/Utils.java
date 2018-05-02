package zylro.atc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * helper utility functions
 *
 * @author wot
 */
public class Utils {

    private static final ObjectMapper OM = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    public static String toJson(Object obj) {
        try {
            return OM.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            LOG.error("Failed to serialize to JSON", ex);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR_500, ex.getMessage());
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return OM.readValue(json, type);
        } catch (IOException ex) {
            LOG.error("Failed to deserialize to object", ex);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR_500, ex.getMessage());
        }
    }
}
