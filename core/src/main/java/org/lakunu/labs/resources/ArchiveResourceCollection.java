package org.lakunu.labs.resources;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class ArchiveResourceCollection extends ResourceCollection {

    private ArchiveFile archive;

    public ArchiveResourceCollection(String path) {
        this.archive = ArchiveFile.newArchiveFile(path);
    }

    @Override
    public void extract(File resourcesDir) throws IOException {
        File tempDir = Files.createTempDirectory("lakunu").toFile();
        try {
            archive.extract(tempDir);
            File[] entries = tempDir.listFiles();
            if (entries != null && entries.length == 1 && entries[0].isDirectory()) {
                logger.info("Copying resource {} to {}", entries[0].getAbsolutePath(),
                        resourcesDir.getAbsolutePath());
                FileUtils.copyDirectory(entries[0], resourcesDir);
            } else {
                logger.info("Copying resource {} to {}", tempDir.getAbsolutePath(),
                        resourcesDir.getAbsolutePath());
                FileUtils.copyDirectory(tempDir, resourcesDir);
            }
        } finally {
            FileUtils.deleteQuietly(tempDir);
        }
    }

}
