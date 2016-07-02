package org.lakunu.labs.phases;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import org.lakunu.labs.Lifecycle;
import org.lakunu.labs.Phase;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public final class DefaultLifecycle implements Lifecycle {

    private static final ImmutableMap<String, Integer> PHASE_ORDER = ImmutableMap.of(
            BuildPhase.NAME, 100,
            RunPhase.NAME, 200
    );

    private static final Comparator<Phase> PHASE_COMPARATOR = (p1,p2) ->
            PHASE_ORDER.get(p1.getName()) - PHASE_ORDER.get(p2.getName());

    private final ImmutableSortedSet<Phase> phases;

    private DefaultLifecycle(List<Phase> phases) {
        checkArgument(phases != null && !phases.isEmpty(), "No phases specified");
        checkArgument(phases.stream().allMatch(p -> p != null), "One or more null entries in phase list");
        Map<String,Long> phaseCounts = phases.stream().collect(Collectors.groupingBy(
                Phase::getName, Collectors.counting()));
        phaseCounts.forEach((p,c) -> {
            checkArgument(PHASE_ORDER.containsKey(p), "Unknown phase: %s", p);
            checkArgument(c == 1, "Duplicate phase: %s", p);
        });
        this.phases = ImmutableSortedSet.orderedBy(PHASE_COMPARATOR).addAll(phases).build();
    }

    @Override
    public Iterator<Phase> getPhases() {
        return phases.iterator();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private final List<Phase> phases = new ArrayList<>();

        private Builder() {
        }

        public Builder add(Phase phase) {
            phases.add(phase);
            return this;
        }

        public DefaultLifecycle build() {
            return new DefaultLifecycle(phases);
        }

    }
}
