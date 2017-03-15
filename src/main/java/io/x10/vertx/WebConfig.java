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

    protected Integer httpPort;

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

    public WebConfig(Integer httpPort, String allowOrigin) {
        this.httpPort = httpPort;
        this.allowOrigin = allowOrigin;
    }
}
