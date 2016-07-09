package org.lakunu.labs.plugins.validators;

import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Score;
import org.lakunu.labs.plugins.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public final class FileExistsValidator extends Validator {

    private final ImmutableList<String> files;
    private final ImmutableList<String> directories;
    private final double scorePerFile;
    private final boolean testNotExists;

    private FileExistsValidator(Builder builder) {
        super(builder);
        checkArgument(!builder.files.isEmpty() || !builder.directories.isEmpty(),
                "specify at least one file or directory to check");
        checkArgument(builder.score < 0 == builder.scorePerFile < 0,
                "scorePerFile must have same sign as score");
        if (builder.score != 0) {
            checkArgument(builder.scorePerFile != 0,
                    "scorePerFile cannot be 0 when score is non-zero");
        }
        this.files = ImmutableList.copyOf(builder.files);
        this.directories = ImmutableList.copyOf(builder.directories);
        this.scorePerFile = builder.scorePerFile;
        this.testNotExists = builder.testNotExists;
    }

    @Override
    public Score validate(Plugin.Context context) {
        long existingFiles = files.stream().map(context::resolvePath)
                .filter(this::checkFile).count();
        long existingDirs = directories.stream().map(context::resolvePath)
                .filter(this::checkDirectory).count();
        return reportScore((existingFiles + existingDirs) * scorePerFile);
    }

    private boolean checkFile(File f) {
        if (testNotExists) {
            return !f.exists();
        } else {
            return f.exists() && f.isFile();
        }
    }

    private boolean checkDirectory(File f) {
        if (testNotExists) {
            return !f.exists();
        } else {
            return f.exists() && f.isDirectory();
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Validator.Builder<FileExistsValidator,Builder> {

        private double scorePerFile;
        private List<String> files = new ArrayList<>();
        private List<String> directories = new ArrayList<>();
        private boolean testNotExists;

        private Builder() {
        }

        public Builder setScorePerFile(double scorePerFile) {
            this.scorePerFile = scorePerFile;
            return this;
        }

        public Builder addFile(String file) {
            this.files.add(file);
            return this;
        }

        public Builder addDirectory(String directory) {
            this.directories.add(directory);
            return this;
        }

        public Builder setTestNotExists(boolean testNotExists) {
            this.testNotExists = testNotExists;
            return this;
        }

        @Override
        protected Builder getThisObj() {
            return this;
        }

        @Override
        public FileExistsValidator build() {
            return new FileExistsValidator(this);
        }
    }
}
