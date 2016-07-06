package org.lakunu.labs.plugins.validators;

import com.google.common.collect.ImmutableMap;

public final class SuccessValidatorFactory extends ValidatorFactory<SuccessValidator> {

    @Override
    public String getName() {
        return "success";
    }

    @Override
    public SuccessValidator build(ImmutableMap<String,Object> properties) {
        String name = getProperty(properties, "name", String.class);
        double score = getNumericProperty(properties, "score");
        return new SuccessValidator(name, score);
    }
}
