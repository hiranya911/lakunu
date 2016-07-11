package org.lakunu.labs.config;

import com.google.common.collect.ImmutableMap;
import org.lakunu.labs.plugins.Plugin;
import org.lakunu.labs.plugins.PluginFactory;

public final class PluginRegistry extends ObjectFactoryRegistry<PluginFactory,Plugin> {

    private static final PluginRegistry instance = new PluginRegistry();

    private PluginRegistry() {
        super(PluginFactory.class);
    }

    public static PluginRegistry getInstance() {
        return instance;
    }

    @Override
    protected String getName(PluginFactory factory) {
        return factory.getName();
    }

    @Override
    protected Plugin newInstance(PluginFactory factory, ImmutableMap<String, Object> properties) {
        return factory.build(properties);
    }
}
