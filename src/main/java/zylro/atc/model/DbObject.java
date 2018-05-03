package zylro.atc.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.Map;

/**
 * base db object with properties needed to do basic db operations
 *
 * @author wot
 */
public class DbObject {

    protected final Map<String, Object> additionalProperties = new HashMap<>();
    //mongodb uses this as the identifying field
    public static String ID_FIELD = "_id";

    @JsonAnySetter
    public void setAdditionalProperties(String key, Object value) {
        this.additionalProperties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
}
