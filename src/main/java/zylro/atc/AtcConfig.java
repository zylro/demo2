package zylro.atc;

import io.dropwizard.Configuration;
import javax.validation.constraints.NotNull;

/**
 * ATC configurations
 *
 * @author wot
 */
public class AtcConfig extends Configuration {

    @NotNull
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}
