package org.lakunu.labs.plugins.validators;

import com.google.common.collect.ImmutableMap;

public final class PluginSuccessValidatorFactory extends ValidatorFactory<PluginSuccessValidator> {

    @Override
    public String getName() {
        return "success";
    }

    @Override
    public PluginSuccessValidator build(ImmutableMap<String,Object> properties) {
        String name = getProperty(properties, "name", String.class);
        double score = getNumericProperty(properties, "score");
        return new PluginSuccessValidator(name, score);
    }
}
