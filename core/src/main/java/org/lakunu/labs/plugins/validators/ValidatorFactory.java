package org.lakunu.labs.plugins.validators;

import com.google.common.collect.ImmutableMap;

public abstract class ValidatorFactory<T extends Validator> {

    public abstract String getName();

    public abstract T build(ImmutableMap<String,Object> properties);

    protected final double getNumericProperty(ImmutableMap<String,Object> properties, String name) {
        Object value = properties.get(name);
        if (value != null) {
            return Number.class.cast(value).doubleValue();
        }
        return 0D;
    }

    protected final <U> U getProperty(ImmutableMap<String,Object> properties, String name, Class<U> clazz) {
        Object value = properties.get(name);
        if (value != null) {
            return clazz.cast(value);
        }
        return null;
    }

    protected final <U> U getProperty(ImmutableMap<String,Object> properties, String name,
                                      U def, Class<U> clazz) {
        U value = getProperty(properties, name, clazz);
        if (value != null) {
            return value;
        }
        return def;
    }

}
