package org.lakunu.labs.config;

import com.google.common.collect.ImmutableMap;
import org.lakunu.labs.plugins.validators.Validator;
import org.lakunu.labs.plugins.validators.ValidatorFactory;

public final class ValidatorRegistry extends ObjectFactoryRegistry<ValidatorFactory,Validator> {

    private static final ValidatorRegistry instance = new ValidatorRegistry();

    private ValidatorRegistry() {
        super(ValidatorFactory.class);
    }

    public static ValidatorRegistry getInstance() {
        return instance;
    }

    @Override
    protected String getName(ValidatorFactory factory) {
        return factory.getName();
    }

    @Override
    protected Validator newInstance(ValidatorFactory factory, ImmutableMap<String, Object> properties) {
        return factory.build(properties);
    }
}
