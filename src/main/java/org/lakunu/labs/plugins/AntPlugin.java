package org.lakunu.labs.plugins;

import com.google.common.base.Strings;
import org.lakunu.labs.LabContext;
import org.lakunu.labs.Plugin;
import org.lakunu.labs.utils.SystemCommand;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

public class AntPlugin implements Plugin {

    private final String antBinary;
    private final String buildTarget;

    public AntPlugin(String antBinary, String buildTarget) {
        checkArgument(!Strings.isNullOrEmpty(antBinary), "Ant binary is required");
        checkArgument(!Strings.isNullOrEmpty(buildTarget), "Ant build target is required");
        this.antBinary = antBinary;
        this.buildTarget = buildTarget;
    }

    @Override
    public boolean execute(LabContext context) {
        try {
            SystemCommand command = SystemCommand.newBuilder()
                    .setCommand(antBinary)
                    .addArgument(buildTarget)
                    .setWorkingDir(context.getWorkingDir())
                    .setOutputHandler(context.getOutputHandler())
                    .build();
            return command.run() == 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
