package io.x10.vertx;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.x10.vertx.data.JsonRequest;

import java.io.IOException;


/**
 * Created by chipn@eway.vn on 1/15/17.
 */
public class JsonUtils {

    private final static ObjectMapper mapper = new ObjectMapper() {
        {
            this.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            this.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            this.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }
    };

    public static <T> JsonRequest<T> decode(RoutingContext context, Class<T> genericClazz) {
        try {
            JsonRequest<T> jsonRequest;
            if (HttpMethod.POST.equals(context.request().method()) || HttpMethod.PUT.equals(context.request().method())) {
                JavaType jsonRequestType = mapper.getTypeFactory().constructParametricType(JsonRequest.class, genericClazz);
                jsonRequest = mapper.readValue(context.getBody().getBytes(), jsonRequestType);

            } else {
                jsonRequest = new JsonRequest<T>();
            }

            jsonRequest.setContext(context);
            return jsonRequest;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Buffer encode(Object object) {
        if (object == null) return null;
        try {
            return Buffer.buffer(mapper.writeValueAsBytes(object));
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode as JSON", e);
        }
    }
}
