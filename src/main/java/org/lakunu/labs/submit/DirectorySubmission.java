package org.lakunu.labs.submit;

import com.google.common.base.Strings;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;

public final class DirectorySubmission extends Submission {

    private final File directory;

    public DirectorySubmission(String path) {
        checkArgument(!Strings.isNullOrEmpty(path), "Directory path is required");
        this.directory = new File(path).getAbsoluteFile();
        checkArgument(this.directory.exists(), "Directory %s does not exist", path);
        checkArgument(this.directory.isDirectory(), "Directory %s is not a directory", path);
    }

    @Override
    public File doGetDirectory() {
        return directory;
    }
}
