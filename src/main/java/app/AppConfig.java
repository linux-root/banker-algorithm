package app;

import io.x10.vertx.WebConfig;

/**
 * Created by chipn@eway.vn on 1/15/17.
 */
public class AppConfig extends WebConfig {

    public AppConfig(Integer httpPort, String allowOrigin) {
        super(httpPort, allowOrigin);
    }
}
