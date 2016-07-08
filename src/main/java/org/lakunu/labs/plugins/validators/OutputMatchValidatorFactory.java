package org.lakunu.labs.plugins.validators;

import com.google.common.collect.ImmutableMap;

public final class OutputMatchValidatorFactory extends ValidatorFactory<OutputMatchValidator> {

    @Override
    public String getName() {
        return "output-match";
    }

    @Override
    public OutputMatchValidator build(ImmutableMap<String, Object> properties) {
        return OutputMatchValidator.newBuilder()
                .setName(getProperty(properties, "name", String.class))
                .setScore(getNumericProperty(properties, "score"))
                .setPattern(getProperty(properties, "pattern", String.class))
                .build();
    }
}
