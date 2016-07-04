package org.lakunu.labs.resources;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.EvaluationContext;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

public final class Resources {

    private final ImmutableSet<Resource> resources;

    public Resources(ImmutableSet<Resource> resources) {
        checkArgument(resources != null, "Resources list is required");
        this.resources = resources;
    }

    public void init(EvaluationContext context) throws IOException {
        if (resources.isEmpty()) {
            return;
        }
        File resourcesDir = new File(context.getEvaluationDirectory(), "_resources");
        FileUtils.forceMkdir(resourcesDir);
        for (Resource resource : resources) {
            resource.copyTo(resourcesDir);
        }
    }

}
