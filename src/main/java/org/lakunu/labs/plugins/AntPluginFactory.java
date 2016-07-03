package org.lakunu.labs.plugins;

import com.google.common.collect.ImmutableMap;
import org.lakunu.labs.PluginFactory;

public final class AntPluginFactory extends PluginFactory<AntPlugin> {

    @Override
    public String getName() {
        return "ant";
    }

    @Override
    public AntPlugin build(ImmutableMap<String, Object> properties) {
        return AntPlugin.newBuilder()
                .setAntBinary(getProperty(properties, "binary", "ant", String.class))
                .setBuildTarget(getProperty(properties, "target", "compile", String.class))
                .setFailOnError(isFailOnError(properties))
                .build();
    }
}
