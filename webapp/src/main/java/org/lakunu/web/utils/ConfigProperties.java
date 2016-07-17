package org.lakunu.web.utils;

import com.google.common.base.Strings;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class ConfigProperties {

    private final Properties properties;

    public ConfigProperties(ServletContext context, String path) throws IOException {
        checkNotNull(context, "servlet context is required");
        checkArgument(!Strings.isNullOrEmpty(path), "path is required");
        this.properties = new Properties();
        try (InputStream in = context.getResourceAsStream(path)) {
            checkNotNull(in, "failed to load resource: %s", path);
            this.properties.load(in);
        }
    }

    public String getRequired(String key) {
        String value = properties.getProperty(key);
        checkArgument(!Strings.isNullOrEmpty(value), "%s is required", key);
        return value;
    }

    public String getOptional(String key, String def) {
        return properties.getProperty(key, def);
    }

}
