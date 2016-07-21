package org.lakunu.labs.resources;

import org.lakunu.labs.Evaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class ArchiveResourceCollection implements ResourceCollection {

    public static final String ARCHIVE_EXTRACT_ROOT = "archive.extract.root";

    private static final Logger logger = LoggerFactory.getLogger(ArchiveResourceCollection.class);

    private ArchiveFile archive;

    public ArchiveResourceCollection(String path) {
        this.archive = ArchiveFile.newArchiveFile(path);
    }

    @Override
    public File lookup(String name, Evaluation.Context context) {
        try {
            File file = new File(getExtractRoot(context), name);
            if (file.exists()) {
                return file;
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to access the archive", e);
        }
    }

    public File getExtractRoot(Evaluation.Context context) throws IOException {
        File extractRoot = context.getProperty(ARCHIVE_EXTRACT_ROOT, File.class);
        if (extractRoot == null) {
            synchronized (context) {
                extractRoot = context.getProperty(ARCHIVE_EXTRACT_ROOT, File.class);
                if (extractRoot == null) {
                    File tempDir = Files.createTempDirectory(
                            context.getEvaluationDirectory().toPath(), "_resources").toFile();
                    archive.extract(tempDir);
                    File[] entries = tempDir.listFiles();
                    if (entries != null && entries.length == 1 && entries[0].isDirectory()) {
                        extractRoot = entries[0];
                    } else {
                        extractRoot = tempDir;
                    }
                    context.setProperty(ARCHIVE_EXTRACT_ROOT, extractRoot);
                    logger.info("Using extract directory: {}", extractRoot.getAbsolutePath());
                }
            }
        }
        return extractRoot;
    }

}
