package org.lakunu.labs;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import org.lakunu.labs.utils.LabUtils;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Controls the order of phase execution.
 */
public final class Lifecycle {

    private static final Pattern PHASE_NAME = Pattern.compile("^[A-Za-z][\\-A-Za-z0-9]*$");

    private final ImmutableList<String> phaseOrder;
    private final ImmutableListMultimap<String,Plugin> plugins;

    protected Lifecycle(List<String> phaseOrder, ListMultimap<String,Plugin> plugins) {
        checkArgument(phaseOrder != null && !phaseOrder.isEmpty(),
                "Phase order must not be null or empty");
        Map<String,Long> counts = phaseOrder.stream().collect(
                Collectors.groupingBy(p -> p, Collectors.counting()));
        counts.keySet().forEach(phase -> {
            checkArgument(PHASE_NAME.matcher(phase).matches(), "Invalid phase name: %s", phase);
            checkArgument(counts.get(phase) == 1, "Duplicate phase: %s", phase);
        });
        checkArgument(plugins != null && !plugins.isEmpty(),
                "Plugins map must not be null or empty");
        plugins.keySet().forEach(phase ->
                checkArgument(phaseOrder.contains(phase), "Unknown phase: %s", phase));
        this.phaseOrder = ImmutableList.copyOf(phaseOrder);
        this.plugins = ImmutableListMultimap.copyOf(plugins);
    }

    public void run(LabContext context) {
        phaseOrder.forEach(phase -> {
            ImmutableList<Plugin> pluginList = plugins.get(phase);
            if (!pluginList.isEmpty()) {
                LabUtils.outputTitle("Starting " + phase + " phase", context.getOutputHandler());
                pluginList.forEach(plugin -> plugin.execute(context));
            }
        });
    }

    public ImmutableList<String> getPhaseOrder() {
        return phaseOrder;
    }

    public static Builder newBuilder(String type) {
        checkArgument(!Strings.isNullOrEmpty(type), "Builder type is required");
        if (DefaultLifecycle.NAME.equals(type)) {
            return new DefaultLifecycle();
        } else {
            try {
                Class<? extends Builder> clazz = Lifecycle.class.getClassLoader()
                        .loadClass(type).asSubclass(Builder.class);
                Constructor<? extends Builder> constructor = clazz.getConstructor();
                return constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public abstract static class Builder {

        protected final List<String> phases;
        protected final ListMultimap<String,Plugin> plugins = ArrayListMultimap.create();

        protected Builder(List<String> phases) {
            this.phases = phases;
        }

        public final Builder addPlugin(String phase, Plugin plugin) {
            plugins.put(phase, plugin);
            return this;
        }

        public final Lifecycle build() {
            return new Lifecycle(phases, plugins);
        }

    }

}
