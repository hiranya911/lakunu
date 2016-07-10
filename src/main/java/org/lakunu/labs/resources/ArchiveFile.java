package org.lakunu.labs.resources;

import com.google.common.base.Strings;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ArchiveFile<T extends ArchiveEntry> {

    private static final Logger logger = LoggerFactory.getLogger(ArchiveFile.class);

    private static final PosixFilePermission[] PERMISSIONS = PosixFilePermission.values();
    private static final int INT_MODE_MAX = (1 << PERMISSIONS.length) - 1;
    protected final File file;

    private ArchiveFile(File file) {
        checkNotNull(file, "Archive file is required");
        checkArgument(file.exists() && file.isFile(),
                "Archive file does not exist or is not a regular file");
        this.file = file;
    }

    public final void extract(File dest) throws IOException {
        checkNotNull(dest, "destination directory is required");
        checkArgument(dest.exists() && dest.isDirectory(),
                "destination does not exist or is not a directory");
        doExtract(dest);
    }

    protected abstract void doExtract(File dest) throws IOException;
    protected abstract int getFileMode(T entry);

    protected final void copyStream(InputStream source, File dest) throws IOException {
        try (FileOutputStream out = FileUtils.openOutputStream(dest)) {
            IOUtils.copy(source, out);
        }
    }

    protected final void mapFileMode(T entry, File target) throws IOException {
        if (SystemUtils.IS_OS_UNIX || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
            int mode = (getFileMode(entry) & 0777);
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

    private static abstract class StreamingArchiveFile<T extends ArchiveEntry, U extends ArchiveInputStream>
            extends ArchiveFile<T> {

        private final String compression;

        private StreamingArchiveFile(File file, String compression) {
            super(file);
            this.compression = compression;
        }

        protected abstract U newStream(InputStream in) throws IOException;
        protected abstract T getNextEntry(U stream) throws IOException;

        private U openStream() throws IOException {
            try {
                FileInputStream input = FileUtils.openInputStream(file);
                InputStream temp;
                if (Strings.isNullOrEmpty(compression)) {
                    temp = input;
                } else {
                    CompressorStreamFactory compFactory = new CompressorStreamFactory();
                    temp = compFactory.createCompressorInputStream(compression, input);
                }

                return newStream(temp);
            } catch (CompressorException e) {
                throw new IOException("Error while uncompressing archive: " + file.getAbsolutePath(), e);
            }
        }

        @Override
        protected final void doExtract(File dest) throws IOException {
            try (U input = openStream()) {
                T entry;
                while ((entry = getNextEntry(input)) != null) {
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


    }

    private static final class TarBallArchiveFile
            extends StreamingArchiveFile<TarArchiveEntry,TarArchiveInputStream> {

        private TarBallArchiveFile(File file, String compression) {
            super(file, compression);
        }

        @Override
        protected TarArchiveInputStream newStream(InputStream in) {
            return new TarArchiveInputStream(in);
        }

        @Override
        protected TarArchiveEntry getNextEntry(TarArchiveInputStream stream) throws IOException {
            return stream.getNextTarEntry();
        }

        @Override
        protected int getFileMode(TarArchiveEntry entry) {
            return entry.getMode();
        }
    }

    private static final class ZipArchiveFile extends ArchiveFile<ZipArchiveEntry> {

        private ZipArchiveFile(File file) {
            super(file);
        }

        @Override
        protected void doExtract(File dest) throws IOException {
            ZipFile zip = new ZipFile(file);
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                logger.info("Extracting {}", entry.getName());
                File file = new File(dest, entry.getName());
                if (entry.isDirectory()) {
                    FileUtils.forceMkdir(file);
                } else {
                    FileUtils.forceMkdir(file.getParentFile());
                    copyStream(zip.getInputStream(entry), file);
                }
                mapFileMode(entry, file);
            }
        }

        @Override
        protected int getFileMode(ZipArchiveEntry entry) {
            return entry.getUnixMode();
        }
    }

    public static ArchiveFile newArchiveFile(String path) {
        File file = new File(path);
        String name = file.getName().toLowerCase();
        if (name.endsWith(".zip")) {
            return new ZipArchiveFile(file);
        } else if (name.endsWith(".tar")) {
            return new TarBallArchiveFile(file, null);
        } else if (name.endsWith("tar.gz") || name.endsWith(".tgz")) {
            return new TarBallArchiveFile(file, CompressorStreamFactory.GZIP);
        } else {
            throw new IllegalArgumentException("Unsupported archive file: " + file.getAbsolutePath());
        }
    }

}
