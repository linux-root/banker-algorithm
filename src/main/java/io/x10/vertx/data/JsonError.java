package io.x10.vertx.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Created by chipn@eway.vn@eway.vn on 7/14/2016.
 */
public class JsonError {

    @JsonProperty("message")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonProperty("root_cause_message")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String rootCauseMessage;

    @JsonProperty("detail")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String detail;

    public JsonError() {
    }

    public JsonError(Throwable throwable, boolean debug) {
        this.setMessage(ExceptionUtils.getMessage(throwable));
        this.setRootCauseMessage(ExceptionUtils.getRootCauseMessage(throwable));
        if (debug) {
            this.setDetail(ExceptionUtils.getStackTrace(throwable));
        }
    }

    public JsonError(String message) {
        this.setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRootCauseMessage() {
        return rootCauseMessage;
    }

    public void setRootCauseMessage(String rootCauseMessage) {
        this.rootCauseMessage = rootCauseMessage;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


}
