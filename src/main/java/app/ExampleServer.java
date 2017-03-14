package app;

import app.model.Model;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.x10.vertx.JsonUtils;
import io.x10.vertx.WebServer;
import io.x10.vertx.data.JsonMeta;
import io.x10.vertx.data.JsonRequest;
import io.x10.vertx.data.JsonResponse;
import org.bson.Document;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by chipn@eway.vn on 1/15/17.
 */
public class ExampleServer extends WebServer<AppConfig> {

    private String CLIENT_ID = "306684131134-vlgl9ibaoo8fboo2o1dsrgv24h2ov648.apps.googleusercontent.com";
    private HttpTransport transport;
    private com.google.api.client.json.JsonFactory jsonFactory;
    private final JsonObject JWTConfig = new JsonObject()
            .put("keyStore", new JsonObject().put("path", "keystore.jceks").put("type", "jceks").put("password", "secret"));
    private JWTAuth authProvider;
    private Repository repository = new Repository();
    public ExampleServer(Vertx vertx, AppConfig config) {
        super(vertx, config);
        authProvider = JWTAuth.create(vertx, JWTConfig);
    }

    private final JWTOptions jwtOptions = new JWTOptions()
            .setExpiresInMinutes(Long.parseLong("60"))
            .addPermission("get")
            .setAlgorithm("HS256");

    @Override
    protected void setupRouter(Router router) throws Exception {
        this.setupCorsHandler(router);
        router.route("/v1/*").handler(JWTAuthHandler.create(authProvider, "/v1/authorization.json"));
        router.post("/v1/authorization.json").handler(this::generateJWT);
        router.put("/v1/authorization.json").handler(this::refreshJWT);
        router.get("/v1/models.json").handler(this::getModels);
        router.get("/v1/models/:id.json").handler(this::getModel);
        router.post("/v1/models.json").handler(this::createModel);
        router.put("/v1/models/:id.json").handler(this::updateModel);
        router.delete("/v1/models/:id.json").handler(this::deleteModel);
    }

    private void generateJWT(RoutingContext context) {
        //verify
        String idTokenString = context.request().getParam("gtoken"); //get google token from client's HTTPS POST
        try {
            transport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonFactory = new JacksonFactory();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            // Print user identifier
            String userId   = payload.getSubject();
            String name     = (String) payload.get("name");
            String email    = payload.getEmail();
            String theToken = authProvider.generateToken(
                    new JsonObject()
                            .put("email", email)
                            .put("name", name)
                            .put("user_id", userId),
                    jwtOptions);
            Document data = new Document()
                    .append("access_token", theToken)
                    .append("expires", 60);
            JsonResponse<Document> jsonResponse = new JsonResponse<>(context, 200);
            jsonResponse.setData(data);
            jsonResponse.write();
        } else {
            JsonResponse<Document> jsonResponse = new JsonResponse<>(context, 401);
            jsonResponse.setData(new Document());
            jsonResponse.write();
        }
    }

    private void refreshJWT(RoutingContext context) {
        // nhan refresh token
        // check xem no da het han chua
        // neu chua het han thi renew han moi
        // neu het han roi thi fail. 401.

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

    private void displayHomePage(RoutingContext context) {
        JsonResponse<String> jsonResponse = new JsonResponse<>(context, 200);
        jsonResponse.setData("everything is ok");
        jsonResponse.write();
    }

    private void getModels(RoutingContext context) {
        JsonResponse<List<Model>> jsonResponse = new JsonResponse<>(context, 200);
        jsonResponse.setData(new ArrayList<>(repository.getModels().values()));
        jsonResponse.write();
    }

    private void getModel(RoutingContext context) {
        JsonRequest<Model> jsonRequest = JsonUtils.decode(context, Model.class);
        String id = jsonRequest.getContext().request().getParam("id");

        Model model = repository.getModel(id);

        JsonResponse<Model> jsonResponse = new JsonResponse<>(context, 200);
        jsonResponse.setData(model);
        jsonResponse.write();
    }

    private void createModel(RoutingContext context) {
        JsonRequest<Model> jsonRequest = JsonUtils.decode(context, Model.class);
        Model createdModel = repository.create(jsonRequest.getData());

        JsonResponse<Model> jsonResponse = new JsonResponse<>(context, 201);
        jsonResponse.setData(createdModel);
        jsonResponse.write();
    }

    private void updateModel(RoutingContext context) {
        JsonRequest<Model> jsonRequest = JsonUtils.decode(context, Model.class);
        String id = jsonRequest.getContext().request().getParam("id");

        Model updatedModel = repository.update(id, jsonRequest.getData());

        JsonResponse<Model> jsonResponse = new JsonResponse<>(context, 200);
        jsonResponse.setData(updatedModel);
        jsonResponse.write();
    }

    private void deleteModel(RoutingContext context) {
        JsonRequest<Model> jsonRequest = JsonUtils.decode(context, Model.class);
        String id = jsonRequest.getContext().request().getParam("id");

        Model deletedModel = repository.delete(id);

        JsonResponse<Model> jsonResponse = new JsonResponse<>(context, 200);
        jsonResponse.setData(deletedModel);
        jsonResponse.write();
    }

}
