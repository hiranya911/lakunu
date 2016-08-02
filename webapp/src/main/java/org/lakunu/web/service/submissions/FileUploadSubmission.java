package org.lakunu.web.service.submissions;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.resources.ArchiveFile;
import org.lakunu.labs.submit.ArchiveSubmission;
import org.lakunu.labs.submit.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

public final class FileUploadSubmission implements UserSubmission {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadSubmission.class);

    private final String fileName;
    private final byte[] fileData;

    public FileUploadSubmission(String fileName, byte[] fileData) {
        checkArgument(!Strings.isNullOrEmpty(fileName), "FileName is required");
        checkArgument(!fileName.contains(File.separator), "Filename must not be a path");
        checkArgument(ArchiveFile.isSupported(fileName), "Unsupported file type");
        this.fileName = fileName.toLowerCase();
        this.fileData = fileData;
    }

    @Override
    public String getType() {
        if (fileName.endsWith(ArchiveFile.ZIP_FILE_TYPE)) {
            return ZIP_FILE_UPLOAD;
        } else if (fileName.endsWith(ArchiveFile.TAR_FILE_TYPE)) {
            return TAR_FILE_UPLOAD;
        } else if (fileName.endsWith(ArchiveFile.TAR_GZ_FILE_TYPE) ||
                fileName.endsWith(ArchiveFile.TGZ_FILE_TYPE)) {
            return TAR_GZ_FILE_UPLOAD;
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    @Override
    public byte[] getData() {
        return fileData;
    }

    @Override
    public Submission toSubmission(File parentDir) throws IOException {
        File submissionFile = new File(parentDir, fileName);
        FileUtils.writeByteArrayToFile(submissionFile, fileData);
        logger.info("Wrote submission archive file to: {}", submissionFile.getPath());
        return new ArchiveSubmission(submissionFile);
    }
}
