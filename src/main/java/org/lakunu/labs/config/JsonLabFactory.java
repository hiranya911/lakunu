package org.lakunu.labs.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.lakunu.labs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public final class JsonLabFactory implements LabFactory {

    private static final Logger logger = LoggerFactory.getLogger(JsonLabFactory.class);

    private final JsonNode json;

    public JsonLabFactory(InputStream in) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        this.json = mapper.readTree(in);
    }

    @Override
    public Lab build() {
        String lifecycle = getField(json, "_lifecycle", DefaultLifecycle.NAME);
        Lifecycle.Builder builder = Lifecycle.newBuilder(lifecycle);
        Iterator<Map.Entry<String,JsonNode>> fields = json.fields();
        while (fields.hasNext()) {
            Map.Entry<String,JsonNode> entry = fields.next();
            if (entry.getKey().startsWith("_")) {
                continue;
            }
            String phase = entry.getKey();
            logger.info("Setting up phase: {}", phase);
            JsonNode value = entry.getValue();
            if (value.isArray()) {
                for (int i = 0; i < value.size(); i++) {
                    builder.addPlugin(phase, newPlugin(value.get(i)));
                }
            } else {
                builder.addPlugin(phase, newPlugin(value));
            }
        }

        String name = getField(json, "_name", "anonymous");
        return Lab.newBuilder()
                .setName(name)
                .setLifecycle(builder.build())
                .build();
    }

    private Plugin newPlugin(JsonNode node) {
        String plugin = getField(node, "plugin", null);
        checkArgument(!Strings.isNullOrEmpty(plugin), "Plugin name is required");
        logger.info("Setting up plugin: {}", plugin);
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> properties = mapper.convertValue(node, Map.class);
        return PluginRegistry.getInstance().getPlugin(plugin, ImmutableMap.copyOf(properties));
    }

    private String getField(JsonNode node, String name, String def) {
        JsonNode field = node.get(name);
        if (field != null) {
            return field.asText(def);
        } else {
            return def;
        }
    }
}
