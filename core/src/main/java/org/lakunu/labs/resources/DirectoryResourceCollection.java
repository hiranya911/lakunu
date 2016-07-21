package org.lakunu.labs.resources;

import com.google.common.base.Strings;
import org.lakunu.labs.Evaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;

public final class DirectoryResourceCollection implements ResourceCollection {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryResourceCollection.class);

    private final File parent;

    public DirectoryResourceCollection(String path) {
        checkArgument(!Strings.isNullOrEmpty(path), "File path is required");
        this.parent = new File(path).getAbsoluteFile();
        checkArgument(this.parent.exists() && this.parent.isDirectory(),
                "File %s does not exist or is not a directory", path);
        logger.debug("Initialized directory resource collection from path: {}", path);
    }

    @Override
    public File lookup(String name, Evaluation.Context context) {
        File child = new File(parent, name);
        if (child.exists()) {
            return child;
        }
        return null;
    }

}
