package org.lakunu.labs.resources;

import com.google.common.base.Strings;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class ArchiveResourceCollection extends ResourceCollection {

    private File archive;

    public ArchiveResourceCollection(String path) {
        checkArgument(!Strings.isNullOrEmpty(path), "File path is required");
        this.archive = new File(path).getAbsoluteFile();
        checkArgument(this.archive.exists(), "File %s does not exist", path);
        logger.info("Initialized resource collection from path: {}", path);
    }

    @Override
    public void copyTo(File evaluationDir) throws IOException {

    }

    private static class Archive {
        private final CompressorStreamFactory compressor;
        private final ArchiveStreamFactory archiver;

        public Archive(CompressorStreamFactory compressor, ArchiveStreamFactory archiver) {
            checkNotNull(archiver, "archiver is required");
            this.compressor = compressor;
            this.archiver = archiver;
        }

    }
}
