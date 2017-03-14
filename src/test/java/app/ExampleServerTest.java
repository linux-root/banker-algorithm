package app;

import com.google.common.collect.ImmutableMap;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.x10.ConfigFactory;
import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.ServerSocket;

import static io.restassured.RestAssured.*;

/**
 * Created by chipn@eway.vn on 1/16/17.
 */
@RunWith(VertxUnitRunner.class)
public class ExampleServerTest extends TestCase {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();

        ServerSocket socket = new ServerSocket(0);
        RestAssured.port = socket.getLocalPort();
        socket.close();

        AppConfig appConfig = ConfigFactory.getConfig("config1.yml", AppConfig.class);
        appConfig.setHttpPort(RestAssured.port);
        appConfig.validate();

        ExampleServer exampleServer = new ExampleServer(vertx, appConfig);

        Async async = context.async();
        exampleServer.start(event -> {
            if (event.succeeded()) {
                async.complete();
            } else {
                context.fail();
            }
        });
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testStaticIndex() {
        get("/static").then().statusCode(200);
    }

    @Test
    public void testGetModels() {
        given()
                .body(ImmutableMap.of("data", ImmutableMap.of("title", "test", "number", 1)))
                .post("v1/models.json");
        given()
                .body(ImmutableMap.of("data", ImmutableMap.of("title", "test", "number", 2)))
                .post("v1/models.json");

        Response response = given()
                .get("v1/models.json");

        assertEquals(200, response.getStatusCode());

        assertTrue(response.jsonPath().getList("data").size() >= 2);
    }

    @Test
    public void testGetModel() {
        String databaseId = given()
                .body(ImmutableMap.of("data", ImmutableMap.of("title", "test", "number", 1)))
                .post("v1/models.json").jsonPath().getString("data.database_id");

        given()
                .get("v1/models/{id}.json", databaseId)
                .then()
                .body("data.database_id", Matchers.is(databaseId));
    }


    @Test
    public void testCreateModel() {
        given()
                .body(ImmutableMap.of("data", ImmutableMap.of("title", "test", "number", 1)))
                .post("v1/models.json")
                .then()
                .statusCode(201)
                .body(
                        "data.database_id", Matchers.notNullValue(),
                        "data.title", Matchers.is("test"),
                        "data.number", Matchers.is(1),
                        "data.created_at", Matchers.greaterThan(0L)
                );
    }

    @Test
    public void testUpdateModel() {
        String databaseId = given()
                .body(ImmutableMap.of("data", ImmutableMap.of("title", "test", "number", 1, "description", "blah blah")))
                .post("v1/models.json").jsonPath().getString("data.database_id");

        given()
                .body(ImmutableMap.of("data", ImmutableMap.of("title", "test2", "number", 2)))
                .put("v1/models/{id}.json", databaseId)
                .then()
                .statusCode(200)
                .body(
                        "data.database_id", Matchers.equalTo(databaseId),
                        "data.title", Matchers.equalTo("test2"),
                        "data.number", Matchers.is(2),
                        "data.updated_at", Matchers.greaterThan(0L)
                );
    }

    @Test
    public void testDeleteModel() {
        String databaseId = given()
                .body(ImmutableMap.of("data", ImmutableMap.of("title", "test", "number", 1, "description", "blah blah")))
                .post("v1/models.json").jsonPath().getString("data.database_id");

        delete("v1/models/{id}.json", databaseId)
                .then()
                .statusCode(200)
                .body("data.database_id", Matchers.equalTo(databaseId));
    }

    @Test
    public void testCors() {
        given()
                .header(new Header("Origin", "http://unsafedomain.com"))
                .get("v1/models.json")
                .then()
                .statusCode(403);

        given()
                .header(new Header("Origin", "http://safedomain.com"))
                .get("v1/models.json")
                .then()
                .statusCode(200);
    }
}
