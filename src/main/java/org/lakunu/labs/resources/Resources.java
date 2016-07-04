package org.lakunu.labs.resources;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.Evaluation;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

public final class Resources {

    private final ImmutableSet<Resource> resources;

    public Resources(ImmutableSet<Resource> resources) {
        checkArgument(resources != null, "Resources list is required");
        this.resources = resources;
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
