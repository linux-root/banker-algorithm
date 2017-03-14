package io.x10;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by chipn@eway.vn on 1/20/17.
 */
public class ConfigFactory {

    protected static ObjectMapper mapper = new ObjectMapper(new YAMLFactory()) {
        {
            this.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }
    };

    public static <T> T getConfig(String resourceName, Class<T> configClass) throws IOException {
        return getConfig(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName), configClass);
    }

    public static <T> T getConfig(File file, Class<T> configClass) throws IOException {
        Validate.isTrue(file.exists());
        return getConfig(new FileInputStream(file), configClass);
    }

    public static <T> T getConfig(InputStream inputStream, Class<T> configClass) throws IOException {
        Validate.notNull(inputStream);
        return mapper.readValue(inputStream, configClass);
    }
}
