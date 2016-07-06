package org.lakunu.labs.plugins.validators;

import com.google.common.collect.ImmutableMap;

public final class OutputMatchValidatorFactory extends ValidatorFactory<OutputMatchValidator> {

    @Override
    public String getName() {
        return "output-match";
    }

    @Override
    public OutputMatchValidator build(ImmutableMap<String, Object> properties) {
        String name = getProperty(properties, "name", String.class);
        double score = getNumericProperty(properties, "score");
        String pattern = getProperty(properties, "pattern", String.class);
        return new OutputMatchValidator(name, score, pattern);
    }
}
