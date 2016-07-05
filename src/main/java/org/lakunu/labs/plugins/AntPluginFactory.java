package org.lakunu.labs.plugins;

import com.google.common.collect.ImmutableMap;

public final class AntPluginFactory extends PluginFactory<AntPlugin> {

    @Override
    public String getName() {
        return "ant";
    }

    @Override
    public Plugin.Builder<AntPlugin, ?> doBuild(ImmutableMap<String, Object> properties) {
        return AntPlugin.newBuilder()
                .setAntBinary(getProperty(properties, "binary", "ant", String.class))
                .setBuildTarget(getProperty(properties, "target", "compile", String.class))
                .setFailOnError(isFailOnError(properties));
    }
}
