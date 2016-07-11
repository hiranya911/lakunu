package org.lakunu.labs.plugins.validators;

import com.google.common.collect.ImmutableMap;

import java.util.List;

public class AntTestValidatorFactory extends ValidatorFactory<AntTestValidator> {

    @Override
    public String getName() {
        return "ant-test";
    }

    @Override
    public AntTestValidator build(ImmutableMap<String, Object> properties) {
        AntTestValidator.Builder builder = AntTestValidator.newBuilder()
                .setName(getProperty(properties, "name", String.class))
                .setScore(getNumericProperty(properties, "score"))
                .setScorePerTest(getNumericProperty(properties, "scorePerTest"));
        List<?> suites = getProperty(properties, "suites", List.class);
        if (suites != null) {
            suites.forEach(s -> builder.addTestSuite(s.toString()));
        }
        return builder.build();
    }
}
