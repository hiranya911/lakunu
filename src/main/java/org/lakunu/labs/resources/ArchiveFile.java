package org.lakunu.labs.resources;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class ArchiveFile {

    private static final Logger logger = LoggerFactory.getLogger(ArchiveFile.class);

    private static final PosixFilePermission[] PERMISSIONS = PosixFilePermission.values();
    private static final int INT_MODE_MAX = (1 << PERMISSIONS.length) - 1;
    private static final ImmutableMap<String,ArchiveInfo> ARCHIVE_TYPES =
            ImmutableMap.<String,ArchiveInfo>builder()
                    .put(".tar.gz", new ArchiveInfo(ArchiveStreamFactory.TAR, CompressorStreamFactory.GZIP))
                    .put(".tar", new ArchiveInfo(ArchiveStreamFactory.TAR, null))
                    .put(".zip", new ArchiveInfo(ArchiveStreamFactory.ZIP, null))
                    .build();
    private final File file;
    private final String archiver;
    private final String compressor;

    public ArchiveFile(File file) {
        checkNotNull(file, "Archive file is required");
        checkArgument(file.exists() && file.isFile(),
                "Archive file does not exist or is not a regular file");
        ArchiveInfo info = findInfo(file);
        checkNotNull(info, "Unsupported archive type: {}", file.getName());
        this.file = file;
        this.archiver = info.archiver;
        this.compressor = info.compressor;
    }

    private ArchiveInfo findInfo(File file) {
        String fileName = file.getName().toLowerCase();
        for (Map.Entry<String,ArchiveInfo> entry : ARCHIVE_TYPES.entrySet()) {
            if (fileName.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private ArchiveInputStream openStream() throws IOException {
        try {
            FileInputStream input = FileUtils.openInputStream(file);
            InputStream temp;
            if (Strings.isNullOrEmpty(compressor)) {
                temp = input;
            } else {
                CompressorStreamFactory compFactory = new CompressorStreamFactory();
                temp = compFactory.createCompressorInputStream(compressor, input);
            }

            ArchiveStreamFactory archFactory = new ArchiveStreamFactory();
            return archFactory.createArchiveInputStream(archiver, temp);
        } catch (Exception e) {
            throw new IOException("Error opening the archive file: " + file.getAbsolutePath(), e);
        }
    }

    public void extract(File dest) throws IOException {
        checkNotNull(dest, "destination directory is required");
        checkArgument(dest.exists() && dest.isDirectory(),
                "destination does not exist or is not a directory");
        try (ArchiveInputStream input = openStream()) {
            ArchiveEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                logger.info("Extracting {}", entry.getName());
                File file = new File(dest, entry.getName());
                if (entry.isDirectory()) {
                    FileUtils.forceMkdir(file);
                } else {
                    FileUtils.forceMkdir(file.getParentFile());
                    copyStream(input, file);
                }
                mapFileMode(entry, file);
            }
        }
    }

    private void mapFileMode(ArchiveEntry entry, File target) throws IOException {
        if (SystemUtils.IS_OS_UNIX || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
            int mode = (getMode(entry) & 0777);
            logger.error("Setting permission to {}", mode);
            if (mode > 0) {
                // Some code borrowed with thanks from https://github.com/fge/java7-fs-more/
                // Refer their PosixModes class for the full implementation.
                checkArgument((mode & INT_MODE_MAX) == mode, "invalid int mode: %s", mode);
                Set<PosixFilePermission> grants = new HashSet<>();
                for (int i = 0; i < PERMISSIONS.length; i++) {
                    if ((mode & 1) == 1) {
                        grants.add(PERMISSIONS[PERMISSIONS.length - i - 1]);
                    }
                    mode >>= 1;
                }
                Files.setPosixFilePermissions(target.toPath(), grants);
            }
        }
    }

    private int getMode(ArchiveEntry entry) {
        if (entry instanceof ZipArchiveEntry) {
            return ((ZipArchiveEntry) entry).getUnixMode();
        } else if (entry instanceof TarArchiveEntry) {
            return ((TarArchiveEntry) entry).getMode();
        } else {
            return 0;
        }
    }

    private static void copyStream(InputStream source, File dest) throws IOException {
        try (FileOutputStream out = FileUtils.openOutputStream(dest)) {
            IOUtils.copy(source, out);
        }
    }

    private static class ArchiveInfo {
        private final String archiver;
        private final String compressor;

        private ArchiveInfo(String archiver, String compressor) {
            checkArgument(!Strings.isNullOrEmpty(archiver), "archiver is required");
            this.archiver = archiver;
            this.compressor = compressor;
        }
    }
}
