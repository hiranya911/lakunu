package org.lakunu.labs.plugins;

import com.google.common.collect.ImmutableMap;

public final class AntPluginFactory extends PluginFactory<AntPlugin> {

    @Override
    public String getName() {
        return "ant";
    }

    @Override
    public Plugin.Builder<AntPlugin,AntPlugin.Builder> doBuild(
            ImmutableMap<String, Object> properties) {
        AntPlugin.Builder builder = AntPlugin.newBuilder()
                .setAntBinary(getProperty(properties, "binary", "ant", String.class))
                .setBuildTarget(getProperty(properties, "target", "compile", String.class))
                .setProcessStderr(getProperty(properties, "stderr", false, Boolean.class));
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
