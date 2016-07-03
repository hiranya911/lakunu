package org.lakunu.labs.run;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.labs.LabContext;
import org.lakunu.labs.Plugin;
import org.lakunu.labs.utils.SystemCommand;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class RunCommandPlugin implements Plugin {

    private final String command;
    private final ImmutableList<String> args;

    public RunCommandPlugin(String command, List<String> args) {
        checkArgument(!Strings.isNullOrEmpty(command), "Command is required");
        this.command = command;
        this.args = args != null ? ImmutableList.copyOf(args) : null;
    }

    @Override
    public boolean execute(LabContext context) {
        SystemCommand.Builder builder = SystemCommand.newBuilder()
                .setCommand(command)
                .setOutputHandler(context.getOutputHandler())
                .setWorkingDir(context.getWorkingDir());
        if (args != null) {
            args.forEach(builder::addArgument);
        }
        SystemCommand command = builder.build();
        try {
            return command.run() == 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
