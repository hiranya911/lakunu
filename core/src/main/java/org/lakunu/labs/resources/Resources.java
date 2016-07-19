package org.lakunu.labs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Booleans;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.Evaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

public final class Resources {

    private static final Logger logger = LoggerFactory.getLogger(Resources.class);

    private final ImmutableSet<Resource> resources;
    private final ResourceCollection collection;

    private Resources(Builder builder) {
        ImmutableSet<Resource> resources = builder.resources.build();
        int count = Booleans.countTrue(!resources.isEmpty(), builder.collection != null);
        checkArgument(count != 2, "Cannot specify both resources and a resource collection");
        this.resources = resources;
        this.collection = builder.collection;
    }

    public File prepare(Evaluation.Context context) throws IOException {
        if (resources.isEmpty() && collection == null) {
            return null;
        }
        File resourcesDir = new File(context.getEvaluationDirectory(), "_resources");
        FileUtils.forceMkdir(resourcesDir);
        logger.info("Created resources directory: {}", resourcesDir.getAbsolutePath());
        for (Resource resource : resources) {
            resource.copyTo(resourcesDir);
        }

        if (collection != null) {
            collection.extract(resourcesDir);
        }
        return resourcesDir;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private final ImmutableSet.Builder<Resource> resources = ImmutableSet.builder();
        private ResourceCollection collection;

        private Builder() {
        }

        public final Builder addResource(Resource resource) {
            this.resources.add(resource);
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
