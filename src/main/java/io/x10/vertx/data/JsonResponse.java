package io.x10.vertx.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.ext.web.RoutingContext;
import io.x10.vertx.JsonUtils;

/**
 * Created by chipn@eway.vn@eway.vn on 7/13/2016.
 */

public class JsonResponse<T> {

    @JsonIgnore
    private RoutingContext context;

    @JsonProperty("status_code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer statusCode = 200;

    @JsonProperty("meta")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonMeta meta = new JsonMeta();

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonProperty("error")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonError error;

    public JsonResponse(RoutingContext context, Integer statusCode) {
        this.context = context;
        this.statusCode = statusCode;
    }

    public RoutingContext getContext() {
        return context;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public JsonMeta getMeta() {
        return meta;
    }

    public void setMeta(JsonMeta meta) {
        this.meta = meta;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public JsonError getError() {
        return error;
    }

    public void setError(JsonError error) {
        this.error = error;
    }

    public void setError(String message) {
        this.error = new JsonError(message);
    }

    public void setError(Throwable throwable, boolean debug) {
        this.error = new JsonError(throwable, debug);
    }

    public void write() {
        context.response().setStatusCode(statusCode)
                .putHeader("Content-Type", "application/json;charset=UTF-8")
                .end(JsonUtils.encode(this));
    }

}
