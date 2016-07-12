package org.lakunu.labs.resources;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

public final class LocalFileResource extends Resource {

    private final File file;

    public LocalFileResource(String path) {
        checkArgument(!Strings.isNullOrEmpty(path), "File path is required");
        this.file = new File(path).getAbsoluteFile();
        checkArgument(this.file.exists(), "File %s does not exist", path);
        logger.debug("Initialized resource from path: {}", path);
    }

    @Override
    public void copyTo(File resourcesDir) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Copying resource {} to {}", file.getAbsolutePath(),
                    resourcesDir.getAbsolutePath());
        }
        if (file.isDirectory()) {
            FileUtils.copyDirectoryToDirectory(file, resourcesDir);
        } else {
            FileUtils.copyFileToDirectory(file, resourcesDir);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof LocalFileResource) {
            LocalFileResource otherFile = (LocalFileResource) other;
            return file.equals(otherFile.file);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }
}
