package io.x10.vertx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chipn@eway.vn on 6/29/2016.
 */
public abstract class WebConfig {

    @JsonIgnore
    protected static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @JsonProperty("http.port")
    protected Integer httpPort;

    @JsonProperty("http.cors.allow-origin")
    protected String allowOrigin;

    public String getAllowOrigin() {
        return allowOrigin;
    }

    public void setAllowOrigin(String allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    public Integer getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort) {
        this.httpPort = httpPort;
    }

    public void validate() {
        Validate.notNull(httpPort, " http.port must be not null");
    }

}
