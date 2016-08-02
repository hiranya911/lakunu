package org.lakunu.web.service.submissions;

import org.lakunu.labs.submit.Submission;

import java.io.File;
import java.io.IOException;

public interface UserSubmission {

    String ZIP_FILE_UPLOAD = "ZIP_FILE_UPLOAD";
    String TAR_FILE_UPLOAD = "TAR_FILE_UPLOAD";
    String TAR_GZ_FILE_UPLOAD = "TAR_GZ_FILE_UPLOAD";

    String getType();
    byte[] getData();
    Submission toSubmission(File parentDir) throws IOException;

}
