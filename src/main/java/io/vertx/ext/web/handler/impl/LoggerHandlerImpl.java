package io.vertx.ext.web.handler.impl;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.impl.Utils;
import io.x10.vertx.IpUtils;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by dangnh@eway.vn on 10/7/2016.
 * Patched to log client's real IP instead of default request.host() method, which may be Load Balancer's IP
 */
public class LoggerHandlerImpl implements LoggerHandler {
    private final io.vertx.core.logging.Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The Date formatter (UTC JS compatible format)
     */
    private final DateFormat dateTimeFormat = Utils.createRFC1123DateTimeFormatter();

    /**
     * log before request or after
     */
    private final boolean immediate;

    /**
     * the current choosen format
     */
    private final LoggerFormat format;

    public LoggerHandlerImpl(boolean immediate, LoggerFormat format) {
        this.immediate = immediate;
        this.format = format;
    }

    public LoggerHandlerImpl(LoggerFormat format) {
        this(false, format);
    }

    private String getClientAddress(SocketAddress inetSocketAddress) {
        if (inetSocketAddress == null) {
            return null;
        }
        return inetSocketAddress.host();
    }

    private void log(RoutingContext context, long timestamp, String remoteClient, HttpVersion version, HttpMethod method, String uri) {
        HttpServerRequest request = context.request();
        long contentLength = 0;
        if (immediate) {
            Object obj = request.headers().get("content-length");
            if (obj != null) {
                try {
                    contentLength = Long.parseLong(obj.toString());
                } catch (NumberFormatException e) {
                    // ignore it and continue
                    contentLength = 0;
                }
            }
        } else {
            contentLength = request.response().bytesWritten();
        }
        String versionFormatted = "-";
        switch (version) {
            case HTTP_1_0:
                versionFormatted = "HTTP/1.0";
                break;
            case HTTP_1_1:
                versionFormatted = "HTTP/1.1";
                break;
        }

        int status = request.response().getStatusCode();
        String message = null;

        switch (format) {
            case DEFAULT:
                String referrer = request.headers().get("referrer");
                String userAgent = request.headers().get("user-agent");
                referrer = referrer == null ? "-" : referrer;
                userAgent = userAgent == null ? "-" : userAgent;

                message = String.format("%s - - [%s] \"%s %s %s\" %d %d \"%s\" \"%s\"",
                        remoteClient,
                        dateTimeFormat.format(new Date(timestamp)),
                        method,
                        uri,
                        versionFormatted,
                        status,
                        contentLength,
                        referrer,
                        userAgent);
                break;
            case SHORT:
                message = String.format("%s - %s %s %s %d %d - %d ms",
                        remoteClient,
                        method,
                        uri,
                        versionFormatted,
                        status,
                        contentLength,
                        (System.currentTimeMillis() - timestamp));
                break;
            case TINY:
                message = String.format("%s %s %d %d - %d ms",
                        method,
                        uri,
                        status,
                        contentLength,
                        (System.currentTimeMillis() - timestamp));
                break;
        }
        doLog(status, message);
    }

    protected void doLog(int status, String message) {
        if (status >= 500) {
            logger.error(message);
        } else if (status >= 400) {
            logger.warn(message);
        } else {
            logger.info(message);
        }
    }

    @Override
    public void handle(RoutingContext context) {
        // common logging data
        long timestamp = System.currentTimeMillis();

        /**
         * @author: dangnh@eway.vn
         * Copy LoggerHandlerImpl.java from https://github.com/vert-x3/vertx-web/blob/master/vertx-web/src/main/java/io/vertx/ext/web/handler/impl/LoggerHandlerImpl.java
         * Replace getClientAddress by IpUtils.getIp
         */
        //String remoteClient = getClientAddress(context.request().remoteAddress());
        String remoteClient = IpUtils.getIp(context.request());

        HttpMethod method = context.request().method();
        String uri = context.request().uri();
        HttpVersion version = context.request().version();

        if (immediate) {
            log(context, timestamp, remoteClient, version, method, uri);
        } else {
            context.addBodyEndHandler(v -> log(context, timestamp, remoteClient, version, method, uri));
        }

        context.next();

    }
}
