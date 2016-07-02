package org.lakunu.labs;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import org.lakunu.labs.phases.DefaultLifecycle;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Lab {

    private final String name;
    private final ImmutableSet<Resource> resources;
    private final ImmutableSortedSet<Phase> phases;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.name), "Name is required");
        checkNotNull(builder.lifecycle, "Lifecycle is required");
        checkArgument(!builder.phases.isEmpty(), "No phases specified");
        checkArgument(builder.phases.stream().allMatch(p -> p != null), "Null phase found");
        Map<String,Long> phaseCounts = builder.phases.stream().collect(Collectors.groupingBy(
                Phase::getName, Collectors.counting()));
        phaseCounts.forEach((p,c) -> {
            checkArgument(builder.lifecycle.isSupported(p), "Unknown phase: %s", p);
            checkArgument(c == 1, "Duplicate phase: %s", p);
        });
        this.name = builder.name;
        this.resources = builder.resources.build();
        this.phases = ImmutableSortedSet.orderedBy(builder.lifecycle.comparator())
                .addAll(builder.phases).build();
    }

    public ImmutableSortedSet<Phase> getPhases() {
        return phases;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private ImmutableSet.Builder<Resource> resources = ImmutableSet.builder();
        private List<Phase> phases = new ArrayList<>();
        private Lifecycle lifecycle = DefaultLifecycle.INSTANCE;

        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder addResource(Resource resource) {
            this.resources.add(resource);
            return this;
        }

        public Builder addPhase(Phase phase) {
            this.phases.add(phase);
            return this;
        }

        public Builder setLifecycle(Lifecycle lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        public Lab build() {
            return new Lab(this);
        }
    }

}
