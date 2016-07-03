package org.lakunu.labs;

import com.google.common.collect.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Controls the order of phase execution.
 */
public final class Lifecycle {

    protected final ImmutableList<String> phaseOrder;
    protected final ImmutableListMultimap<String,Plugin> plugins;

    protected Lifecycle(List<String> phaseOrder, ListMultimap<String,Plugin> plugins) {
        checkArgument(phaseOrder != null && !phaseOrder.isEmpty(),
                "Phase order must not be null or empty");
        Map<String,Long> counts = phaseOrder.stream().collect(
                Collectors.groupingBy(p -> p, Collectors.counting()));
        checkArgument(counts.keySet().stream().allMatch(p -> counts.get(p) == 1),
                "One or more duplicate phases");
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
            pluginList.forEach(plugin -> plugin.execute(context));
        });
    }

    public ImmutableList<String> getPhaseOrder() {
        return phaseOrder;
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
