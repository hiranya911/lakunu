package org.lakunu.labs.plugins;

import com.google.common.collect.ImmutableMap;
import org.lakunu.labs.PluginFactory;

import java.util.List;

public final class RunCommandPluginFactory extends PluginFactory<RunCommandPlugin> {

    @Override
    public String getName() {
        return "run-command";
    }

    @Override
    public RunCommandPlugin build(ImmutableMap<String, Object> properties) {
        String command = getProperty(properties, "command", String.class);
        List args = getProperty(properties, "args", List.class);
        return new RunCommandPlugin(command, args);
    }
}
