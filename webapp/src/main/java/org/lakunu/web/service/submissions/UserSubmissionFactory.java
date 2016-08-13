package org.lakunu.web.service.submissions;

public class UserSubmissionFactory {

    public static UserSubmission create(String type, byte[] data) {
        return new FileUploadSubmission(getFileName("submission", type), data);
    }

    public static String getFileName(String name, String type) {
        if (type.equals(UserSubmission.ZIP_FILE_UPLOAD)) {
            return name + ".zip";
        } else if (type.equals(UserSubmission.TAR_FILE_UPLOAD)) {
            return name + ".tar";
        } else if (type.equals(UserSubmission.TAR_GZ_FILE_UPLOAD)) {
            return name + ".tar.gz";
        } else {
            throw new IllegalArgumentException("Unsupported submission type: " + type);
        }
    }

    public static String getMimeType(String type) {
        if (type.equals(UserSubmission.ZIP_FILE_UPLOAD)) {
            return "application/zip";
        } else if (type.equals(UserSubmission.TAR_FILE_UPLOAD)) {
            return "application/x-tar";
        } else if (type.equals(UserSubmission.TAR_GZ_FILE_UPLOAD)) {
            return "application/gzip";
        } else {
            throw new IllegalArgumentException("Unsupported submission type: " + type);
        }
    }

}
