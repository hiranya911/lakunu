package org.lakunu.labs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Booleans;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.Evaluation;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

public final class Resources {

    private final ImmutableSet<Resource> resources;
    private final ResourceCollection collection;

    private Resources(ImmutableSet<Resource> resources, ResourceCollection collection) {
        int count = Booleans.countTrue(resources != null, collection != null);
        checkArgument(count == 1, "One of resource list or resource collection is required");
        this.resources = resources;
        this.collection = collection;
    }

    public static Resources fromResourceSet(ImmutableSet<Resource> resourceSet) {
        return new Resources(resourceSet, null);
    }

    public static Resources fromResourceCollection(ResourceCollection collection) {
        return new Resources(null, collection);
    }

    public File prepare(Evaluation.Context context) throws IOException {
        if (resources.isEmpty()) {
            return null;
        }
        File resourcesDir = new File(context.getEvaluationDirectory(), "_resources");
        FileUtils.forceMkdir(resourcesDir);
        for (Resource resource : resources) {
            resource.copyTo(resourcesDir);
        }
        return resourcesDir;
    }

}
