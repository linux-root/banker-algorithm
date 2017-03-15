package io.x10.vertx;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.x10.vertx.data.JsonResponse;

/**
 * Created by chipn@eway.vn on 1/15/17.
 */
public abstract class WebServer<T extends WebConfig> {

    static {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WebServer.class);

    protected final T appConfig;
    protected Router router;
    protected Vertx vertx;
    protected HttpServer httpServer;

    public WebServer(Vertx vertx, T appConfig) {
        this.appConfig = appConfig;
        this.vertx = vertx;
        this.router = Router.router(vertx);
    }

    public T getAppConfig() {
        return appConfig;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public void close() {
        try {
            if (httpServer != null) {
                httpServer.close();
            }
        } catch (Exception e) {
        }
        try {
            if (vertx != null) {
                vertx.close();
            }
        } catch (Exception e) {
        }
    }

    public void start(Handler<AsyncResult<HttpServer>> listenHandler) throws Exception {
        try {
            this.setupJsonMapper();
            this.setupHttpServer();

            //=> Setup Router
            this.setupBodyHandler(router);
            this.setupCookieHandler(router);
            this.setupStaticHandler(router);

            this.setupLoggerHandler(router);

            this.setupFaviconHandler(router);

            this.setupFailureHandler(router);
            this.setupNotFoundHandler(router);

            this.setupRouter(router);

            httpServer.requestHandler(router::accept).listen(this.appConfig.getHttpPort(), asyncResultHandler -> {
                if (asyncResultHandler.failed()) {
                    this.close();
                }
                listenHandler.handle(asyncResultHandler);
            });
        } catch (Exception e) {
            this.close();
            throw e;
        }
    }


    protected void setupJsonMapper() {
        Json.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        Json.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Json.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Json.prettyMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        Json.prettyMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Json.prettyMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }


    protected void setupHttpServer() {
        HttpServerOptions httpServerOptions = new HttpServerOptions();
        httpServerOptions.setMaxInitialLineLength(4096 * 4);
        httpServerOptions.setCompressionSupported(true);
        // DEFAULT_MAX_HEADER_SIZE = 8192 bytes
        httpServerOptions.setMaxHeaderSize(8192 * 2);
        this.httpServer = this.vertx.createHttpServer(httpServerOptions);
    }

    protected void setupLoggerHandler(Router router) {
        LoggerHandler loggerHandler = LoggerHandler.create();
        router.route().handler(loggerHandler);
    }

    protected void setupBodyHandler(Router router) {
        BodyHandler bodyHandler = BodyHandler.create();
        router.route().handler(bodyHandler);
    }

    protected void setupCookieHandler(Router router) {
        CookieHandler cookieHandler = CookieHandler.create();
        router.route().handler(cookieHandler);
    }

    protected void setupStaticHandler(Router router) {
        StaticHandler staticHandler = StaticHandler.create();
        //=> Disable file caching
        staticHandler.setCachingEnabled(false);
        router.route("/").handler(staticHandler);
    }

    protected void setupFaviconHandler(Router router) {
        FaviconHandler faviconHandler = FaviconHandler.create();
        router.route().handler(faviconHandler);
    }

    public void setupFailureHandler(Router router) {
        router.route().failureHandler(this::handleFailure);
    }

    public void setupNotFoundHandler(Router router) {
        router.route().last().handler(this::handleNotFound);
    }

    protected abstract void setupRouter(Router router) throws Exception;

    protected void handleFailure(RoutingContext context) {
        //=> Ignore handle routing with null failure
        final Throwable failure = context.failure();
        if (failure == null) {
            context.next();
            return;
        }

        logger.error("handleFailure: " + context.request().path(), failure);

        JsonResponse<Object> jsonResponse = new JsonResponse<>(context, 500);
        jsonResponse.setError(context.failure(), Boolean.valueOf(context.request().getParam("debug")));
        jsonResponse.write();
    }

    protected void handleNotFound(RoutingContext context) {
        JsonResponse<Object> jsonResponse = new JsonResponse<>(context, 404);
        jsonResponse.setError("resource not found");
        jsonResponse.write();
    }

}
