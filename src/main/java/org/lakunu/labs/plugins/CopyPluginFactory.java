package org.lakunu.labs.plugins;

import com.google.common.collect.ImmutableMap;

public final class CopyPluginFactory extends PluginFactory<CopyPlugin> {

    @Override
    public String getName() {
        return "copy";
    }

    @Override
    protected Plugin.Builder<CopyPlugin,CopyPlugin.Builder> doBuild(
            ImmutableMap<String, Object> properties) {
        return CopyPlugin.newBuilder()
                .setFile(getProperty(properties, "file", String.class))
                .setToFile(getProperty(properties, "toFile", String.class))
                .setToDirectory(getProperty(properties, "toDir", String.class));
    }
}
