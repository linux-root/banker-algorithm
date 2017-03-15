package app;

import app.model.Data;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.x10.vertx.JsonUtils;
import io.x10.vertx.WebServer;
import io.x10.vertx.data.JsonRequest;
import io.x10.vertx.data.JsonResponse;
import java.util.ArrayList;


/**
 * Created by khanhdb@eway.vn on 3/14/17.
 */
public class RestServer extends WebServer<AppConfig> {

    public RestServer(Vertx vertx, AppConfig config) {
        super(vertx, config);
    }

    @Override
    protected void setupRouter(Router router) throws Exception {
        this.setupCorsHandler(router);
        router.post("/banker").handler(this::checkSafeStatus);
    }

    private void checkSafeStatus(RoutingContext context) {
        JsonRequest<Data> jsonRequest = JsonUtils.decode(context, Data.class);
        Data data = jsonRequest.getData();
        ArrayList<int[]> result = Banker.checkSafeStatus(data.getM(), data.getN(), data.getAllocation(), data.getMax(), data.getAvailable());
        JsonResponse<ArrayList> jsonResponse = new JsonResponse<>(context, 200);
        jsonResponse.setData(result);
        jsonResponse.write();
    }


    public void setupCorsHandler(Router router) {
        CorsHandler corsHandler = CorsHandler.create(appConfig.getAllowOrigin())
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.PATCH)
                .allowedHeader("Authorization")
                .allowedHeader("Content-Type")
                .allowCredentials(true);
        router.route("/*").handler(corsHandler);
    }
}
