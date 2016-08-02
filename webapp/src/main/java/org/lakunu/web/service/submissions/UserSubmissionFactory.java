package org.lakunu.web.service.submissions;

public class UserSubmissionFactory {

    public static UserSubmission create(String type, byte[] data) {
        if (type.equals(UserSubmission.ZIP_FILE_UPLOAD)) {
            return new FileUploadSubmission("submission.zip", data);
        } else if (type.equals(UserSubmission.TAR_FILE_UPLOAD)) {
            return new FileUploadSubmission("submission.tar", data);
        } else if (type.equals(UserSubmission.TAR_GZ_FILE_UPLOAD)) {
            return new FileUploadSubmission("submission.tar.gz", data);
        } else {
            throw new IllegalArgumentException("Unsupported submission type: " + type);
        }
    }

}
