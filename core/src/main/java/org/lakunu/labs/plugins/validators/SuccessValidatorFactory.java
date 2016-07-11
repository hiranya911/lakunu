package org.lakunu.labs.plugins.validators;

import com.google.common.collect.ImmutableMap;

public final class SuccessValidatorFactory extends ValidatorFactory<SuccessValidator> {

    @Override
    public String getName() {
        return "success";
    }

    @Override
    public SuccessValidator build(ImmutableMap<String,Object> properties) {
        return SuccessValidator.newBuilder()
                .setName(getProperty(properties, "name", String.class))
                .setScore(getNumericProperty(properties, "score"))
                .build();
    }
}
