package org.lakunu.labs.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class ObjectFactoryRegistry<T,U> {

    private final ImmutableMap<String,T> factories;

    protected ObjectFactoryRegistry(Class<T> clazz) {
        ImmutableList<T> factories = ImmutableList.<T>builder()
                .addAll(ServiceLoader.load(clazz))
                .build();
        Map<String,Long> counts = factories.stream().collect(
                Collectors.groupingBy(this::getName, Collectors.counting()));
        ImmutableMap.Builder<String,T> builder = ImmutableMap.builder();
        counts.keySet().forEach(f -> checkArgument(counts.get(f) == 1, "Duplicate factory: %s", f));
        factories.forEach(f -> builder.put(getName(f), f));
        this.factories = builder.build();
    }

    public U getObject(String name, ImmutableMap<String,Object> properties) {
        T factory = factories.get(name);
        checkArgument(factory != null, "Unknown factory: %s", name);
        return newInstance(factory, properties);
    }

    protected abstract String getName(T factory);

    protected abstract U newInstance(T factory, ImmutableMap<String,Object> properties);

}
