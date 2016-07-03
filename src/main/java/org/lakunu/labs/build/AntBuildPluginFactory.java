package org.lakunu.labs.build;

import com.google.common.collect.ImmutableMap;
import org.lakunu.labs.PluginFactory;

public final class AntBuildPluginFactory extends PluginFactory<AntBuildPlugin> {

    @Override
    public String getName() {
        return "ant-build";
    }

    @Override
    public AntBuildPlugin build(ImmutableMap<String, Object> properties) {
        String binary = getProperty(properties, "binary", "ant", String.class);
        String target = getProperty(properties, "target", "compile", String.class);
        return new AntBuildPlugin(binary, target);
    }
}
