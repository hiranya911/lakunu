package org.lakunu.labs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Booleans;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;

public final class Resources {

    private final ImmutableSet<File> files;
    private final ResourceCollection collection;

    private Resources(Builder builder) {
        ImmutableSet<File> resources = builder.resourcesFiles.build();
        int count = Booleans.countTrue(!resources.isEmpty(), builder.collection != null);
        checkArgument(count != 2, "Cannot specify both resource files and a resource collection");
        resources.forEach(r ->
                checkArgument(r.exists(), "resource file does not exist: %s", r.getAbsolutePath()));
        this.files = resources;
        this.collection = builder.collection;
    }

    public ImmutableSet<File> getFiles() {
        return files;
    }

    public ResourceCollection getCollection() {
        return collection;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private final ImmutableSet.Builder<File> resourcesFiles = ImmutableSet.builder();
        private ResourceCollection collection;

        private Builder() {
        }

        public final Builder addFile(File resource) {
            this.resourcesFiles.add(resource);
            return this;
        }

        public Builder setCollection(ResourceCollection collection) {
            this.collection = collection;
            return this;
        }

        public Resources build() {
            return new Resources(this);
        }
    }

}
