package org.lakunu.labs.plugins;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.lakunu.labs.config.ValidatorRegistry;
import org.lakunu.labs.plugins.validators.Validator;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class PluginFactory<T extends Plugin> {

    public abstract String getName();

    public final T build(ImmutableMap<String,Object> properties) {
        Plugin.Builder<T,?> builder = doBuild(properties);
        List<?> validators = (List) properties.get("validators");
        if (validators != null) {
            validators.forEach(obj -> builder.addValidator(newValidator(obj)));
        }
        return builder.build();
    }

    protected abstract Plugin.Builder<T,?> doBuild(ImmutableMap<String,Object> properties);

    private Validator newValidator(Object obj) {
        Map<String,Object> properties = (Map) obj;
        String type = getProperty(properties, "type", String.class);
        checkArgument(!Strings.isNullOrEmpty(type), "validator type is required");
        return ValidatorRegistry.getInstance().getObject(type, ImmutableMap.copyOf(properties));
    }

    protected final <U> U getProperty(Map<String,Object> properties, String name, Class<U> clazz) {
        Object value = properties.get(name);
        if (value != null) {
            return clazz.cast(value);
        }
        return null;
    }

    protected final <U> U getProperty(Map<String,Object> properties, String name,
                                      U def, Class<U> clazz) {
        U value = getProperty(properties, name, clazz);
        if (value != null) {
            return value;
        }
        return def;
    }

    protected final boolean isFailOnError(Map<String,Object> properties) {
        return getProperty(properties, "failOnError", true, Boolean.class);
    }
}
