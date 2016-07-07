package org.lakunu.labs.plugins.validators;

import com.google.common.collect.ImmutableMap;

public class AntTestValidatorFactory extends ValidatorFactory<AntTestValidator> {

    @Override
    public String getName() {
        return "ant-test";
    }

    @Override
    public AntTestValidator build(ImmutableMap<String, Object> properties) {
        String name = getProperty(properties, "name", String.class);
        double maxScore = getNumericProperty(properties, "score");
        double scorePerTest = getNumericProperty(properties, "scorePerTest");
        return new AntTestValidator(name, maxScore, scorePerTest);
    }
}
