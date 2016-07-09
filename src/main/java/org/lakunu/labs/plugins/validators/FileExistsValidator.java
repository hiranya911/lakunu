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
    private final double scorePerFile;
    private final boolean testNotExists;

    private FileExistsValidator(Builder builder) {
        super(builder);
        checkArgument(!builder.files.isEmpty(), "specify at least one file to check");
        checkArgument(builder.score < 0 == builder.scorePerFile < 0,
                "scorePerFile must have same sign as score");
        if (builder.score != 0) {
            checkArgument(builder.scorePerFile != 0, "scorePerFile cannot be 0");
        }
        this.files = ImmutableList.copyOf(builder.files);
        this.scorePerFile = builder.scorePerFile;
        this.testNotExists = builder.testNotExists;
    }

    @Override
    public Score validate(Plugin.Context context) {
        long existing = files.stream().map(context::resolvePath).filter(this::checkFile).count();
        return reportScore(existing * scorePerFile);
    }

    private boolean checkFile(File f) {
        if (testNotExists) {
            return !f.exists();
        } else {
            return f.exists();
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Validator.Builder<FileExistsValidator,Builder> {

        private double scorePerFile;
        private List<String> files = new ArrayList<>();
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
