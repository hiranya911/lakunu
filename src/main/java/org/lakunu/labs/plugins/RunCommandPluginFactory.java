package org.lakunu.labs.plugins;

import com.google.common.collect.ImmutableMap;

import java.util.List;

public final class RunCommandPluginFactory extends PluginFactory<RunCommandPlugin> {

    @Override
    public String getName() {
        return "run-command";
    }

    @Override
    protected Plugin.Builder<RunCommandPlugin,RunCommandPlugin.Builder> doBuild(
            ImmutableMap<String, Object> properties) {
        RunCommandPlugin.Builder builder = RunCommandPlugin.newBuilder()
                .setCommand(getProperty(properties, "command", String.class))
                .setStatus(getProperty(properties, "status", 0, Integer.class));
        List<?> args = getProperty(properties, "args", List.class);
        if (args != null) {
            args.forEach(arg -> builder.addArgument(arg.toString()));
        }
        Integer bufferLimit = getProperty(properties, "stdoutBuffer", Integer.class);
        if (bufferLimit != null) {
            builder.setStdoutBufferLimit(bufferLimit);
        }
        bufferLimit = getProperty(properties, "stderrBuffer", Integer.class);
        if (bufferLimit != null) {
            builder.setStderrBufferLimit(bufferLimit);
        }
        return builder;
    }
}
