package org.lakunu.labs.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.lakunu.labs.*;
import org.lakunu.labs.plugins.Plugin;
import org.lakunu.labs.resources.ArchiveResourceCollection;
import org.lakunu.labs.resources.LocalFileResource;
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
        String builderType = getStringField(json, "_builder", "default");
        Lab.Builder builder;
        if ("default".equals(builderType)) {
            builder = DefaultLabBuilder.newBuilder();
        } else {
            builder = Lab.newLabBuilder(builderType);
        }

        JsonNode resources = json.get("_resources");
        if (resources != null) {
            if (resources.isArray()) {
                for (int i = 0; i < resources.size(); i++) {
                    builder.addResource(new LocalFileResource(resources.get(i).asText()));
                }
            } else {
                builder.addResource(new LocalFileResource(resources.asText()));
            }
        }

        JsonNode collection = json.get("_collection");
        if (collection != null) {
            builder.setCollection(new ArchiveResourceCollection(collection.asText()));
        }

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

        String name = getStringField(json, "_name", "anonymous");
        return builder.setName(name).build();
    }

    private Plugin newPlugin(JsonNode node) {
        String plugin = getStringField(node, "plugin", null);
        checkArgument(!Strings.isNullOrEmpty(plugin), "Plugin name is required");
        logger.info("Setting up plugin: {}", plugin);
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> properties = mapper.convertValue(node,
                new TypeReference<Map<String,Object>>(){});
        return PluginRegistry.getInstance().getObject(plugin, ImmutableMap.copyOf(properties));
    }

    private String getStringField(JsonNode node, String name, String def) {
        JsonNode field = node.get(name);
        if (field != null) {
            return field.asText(def);
        } else {
            return def;
        }
    }

    public static Lab newLab(InputStream in) throws IOException {
        return new JsonLabFactory(in).build();
    }
}
