package org.lakunu.labs.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.lakunu.labs.Plugin;
import org.lakunu.labs.PluginFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public final class PluginRegistry {

    private static final PluginRegistry instance = new PluginRegistry();

    private final ImmutableMap<String,PluginFactory> factories;

    private PluginRegistry() {
        ImmutableList<PluginFactory> factories = ImmutableList.<PluginFactory>builder()
                .addAll(ServiceLoader.load(PluginFactory.class))
                .build();
        Map<String,Long> counts = factories.stream().collect(
                Collectors.groupingBy(PluginFactory::getName, Collectors.counting()));
        ImmutableMap.Builder<String,PluginFactory> builder = ImmutableMap.builder();
        counts.keySet().forEach(p -> checkArgument(counts.get(p) == 1, "Duplicate plugin: %s", p));
        factories.forEach(f -> builder.put(f.getName(), f));
        this.factories = builder.build();
    }

    public static PluginRegistry getInstance() {
        return instance;
    }

    public <T extends Plugin> T getPlugin(String name, Class<T> clazz,
                                          ImmutableMap<String,Object> properties) {
        PluginFactory factory = factories.get(name);
        checkArgument(factory != null, "Unknown plugin: %s", name);
        return clazz.cast(factory.build(properties));
    }

}
