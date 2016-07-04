package org.lakunu.labs;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.plugins.Plugin;
import org.lakunu.labs.resources.Resource;
import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.utils.LabUtils;
import org.lakunu.labs.utils.LoggingOutputHandler;


import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Lab {

    private static final Pattern PHASE_NAME = Pattern.compile("^[A-Za-z][\\-A-Za-z0-9]*$");

    private final String name;
    private final ImmutableList<String> phases;
    private final ImmutableListMultimap<String,Plugin> plugins;
    private final File workingDirectory;
    private final ImmutableSet<Resource> resources;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.name), "Name is required");
        checkArgument(builder.phases != null && !builder.phases.isEmpty(),
                "Phase order must not be null or empty");
        Map<String,Long> counts = builder.phases.stream().collect(
                Collectors.groupingBy(p -> p, Collectors.counting()));
        counts.keySet().forEach(phase -> {
            checkArgument(PHASE_NAME.matcher(phase).matches(), "Invalid phase name: %s", phase);
            checkArgument(counts.get(phase) == 1, "Duplicate phase: %s", phase);
        });
        checkArgument(builder.plugins != null && !builder.plugins.isEmpty(),
                "Plugins map must not be null or empty");
        builder.plugins.keySet().forEach(phase ->
                checkArgument(builder.phases.contains(phase), "Unknown phase: %s", phase));
        checkNotNull(builder.workingDirectory, "Working directory is required");
        checkArgument(builder.workingDirectory.isDirectory() && builder.workingDirectory.exists(),
                "Working directory path is not a directory or does not exist");
        this.phases = ImmutableList.copyOf(builder.phases);
        this.plugins = ImmutableListMultimap.copyOf(builder.plugins);
        this.name = builder.name;
        this.workingDirectory = builder.workingDirectory;
        this.resources = builder.resources.build();
    }

    private void run(EvaluationContext context, String finalPhase) {
        for (String phase : phases) {
            boolean proceed = runPhase(phase, context);
            if (!proceed || phase.equals(finalPhase)) {
                break;
            }
        }
    }

    private boolean runPhase(String phase, EvaluationContext context) {
        ImmutableList<Plugin> pluginList = plugins.get(phase);
        if (pluginList.isEmpty()) {
            return true;
        }

        LabUtils.outputTitle("Starting " + phase + " phase", context.getOutputHandler());
        for (Plugin plugin : pluginList) {
            if (!plugin.execute(context)) {
                return false;
            }
        }
        return true;
    }

    public ImmutableList<String> getPhases() {
        return phases;
    }

    public String getName() {
        return name;
    }

    public void evaluate(Submission submission, String phase) {
        EvaluationContext context = EvaluationContext.newBuilder()
                .setSubmission(submission)
                .setWorkingDirectory(workingDirectory)
                .setOutputHandler(new LoggingOutputHandler())
                .build();
        try {
            run(context, phase);
        } finally {
            context.cleanup();
        }
    }

    public void evaluate(Submission submission) {
        evaluate(submission, null);
    }

    public abstract static class Builder {
        private String name;
        protected final List<String> phases;
        private final ListMultimap<String,Plugin> plugins = ArrayListMultimap.create();
        private File workingDirectory = FileUtils.getTempDirectory();
        private ImmutableSet.Builder<Resource> resources = ImmutableSet.builder();

        protected Builder(List<String> phases) {
            this.phases = phases;
        }

        public final Builder addPlugin(String phase, Plugin plugin) {
            plugins.put(phase, plugin);
            return this;
        }

        public final Builder setName(String name) {
            this.name = name;
            return this;
        }

        public final Builder setWorkingDirectory(File workingDirectory) {
            this.workingDirectory = workingDirectory;
            return this;
        }

        public final Builder addResource(Resource resource) {
            this.resources.add(resource);
            return this;
        }

        public final Lab build() {
            return new Lab(this);
        }
    }

    public static Builder newLabBuilder(String type) {
        try {
            Class<? extends Builder> builder = Lab.class.getClassLoader().loadClass(type)
                    .asSubclass(Builder.class);
            Constructor<? extends Builder> constructor = builder.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
