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
        String binary = getProperty(properties, "binary", "ant", String.class);
        String target = getProperty(properties, "target", "compile", String.class);
        return new AntPlugin(binary, target);
    }
}
