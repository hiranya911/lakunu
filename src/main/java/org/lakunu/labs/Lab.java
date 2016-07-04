package org.lakunu.labs;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import org.lakunu.labs.plugins.Plugin;
import org.lakunu.labs.resources.Resource;
import org.lakunu.labs.resources.Resources;
import org.lakunu.labs.utils.LabUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public final class Lab {

    private static final Pattern PHASE_NAME = Pattern.compile("^[A-Za-z][\\-A-Za-z0-9]*$");

    private final String name;
    private final ImmutableList<String> phases;
    private final ImmutableListMultimap<String,Plugin> plugins;
    private final Resources resources;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.name), "Name is required");

        ImmutableList<String> phases = builder.getPhases();
        checkArgument(phases != null && !phases.isEmpty(),
                "Phase order must not be null or empty");
        Map<String,Long> counts = phases.stream().collect(
                Collectors.groupingBy(p -> p, Collectors.counting()));
        counts.keySet().forEach(phase -> {
            checkArgument(PHASE_NAME.matcher(phase).matches(), "Invalid phase name: %s", phase);
            checkArgument(counts.get(phase) == 1, "Duplicate phase: %s", phase);
        });

        checkArgument(builder.plugins != null && !builder.plugins.isEmpty(),
                "Plugins map must not be null or empty");
        builder.plugins.keySet().forEach(phase ->
                checkArgument(phases.contains(phase), "Unknown phase: %s", phase));

        this.phases = phases;
        this.plugins = ImmutableListMultimap.copyOf(builder.plugins);
        this.name = builder.name;
        this.resources = new Resources(builder.resources.build());
    }

    public String getName() {
        return name;
    }

    public ImmutableList<String> getPhases() {
        return phases;
    }

    public void execute(EvaluationContext context, String finalPhase) throws IOException {
        resources.init(context);
        for (String phase : phases) {
            boolean proceed = runPhase(context, phase);
            if (!proceed || phase.equals(finalPhase)) {
                break;
            }
        }
    }

    private boolean runPhase(EvaluationContext context, String phase) {
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

    public abstract static class Builder {
        private String name;
        private final ListMultimap<String,Plugin> plugins = ArrayListMultimap.create();
        private ImmutableSet.Builder<Resource> resources = ImmutableSet.builder();

        protected Builder() {
        }

        public final Builder addPlugin(String phase, Plugin plugin) {
            plugins.put(phase, plugin);
            return this;
        }

        public final Builder setName(String name) {
            this.name = name;
            return this;
        }

        public final Builder addResource(Resource resource) {
            this.resources.add(resource);
            return this;
        }

        protected abstract ImmutableList<String> getPhases();

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
