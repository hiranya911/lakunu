package org.lakunu.labs.plugins;

import com.google.common.base.Strings;
import com.google.common.primitives.Booleans;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkArgument;

public final class CopyPlugin extends Plugin {

    private final String file;
    private final String toFile;
    private final String toDirectory;

    private CopyPlugin(Builder builder) {
        super(builder);
        checkArgument(!Strings.isNullOrEmpty(builder.file), "Source file is required");
        int targets = Booleans.countTrue(!Strings.isNullOrEmpty(builder.toFile),
                !Strings.isNullOrEmpty(builder.toDirectory));
        checkArgument(targets == 1, "Exactly one of toFile or toDirectory is required");
        this.file = builder.file;
        this.toFile = builder.toFile;
        this.toDirectory = builder.toDirectory;
    }

    @Override
    protected boolean doExecute(Context context) throws Exception {
        File source = new File(context.replaceProperties(file));
        if (toFile != null) {
            File dest = getDestination(context, toFile);
            if (source.isDirectory()) {
                FileUtils.copyDirectory(source, dest);
            } else {
                FileUtils.copyFile(source, dest);
            }
            context.getOutputHandler().info("Copied " + source.getAbsolutePath() + " as " +
                    dest.getAbsolutePath());
        } else {
            File dest = getDestination(context, toDirectory);
            if (source.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(source, dest);
            } else {
                FileUtils.copyFileToDirectory(source, dest);
            }
            context.getOutputHandler().info("Copied " + source.getAbsolutePath() + " into " +
                    dest.getAbsolutePath());
        }
        context.getOutputHandler().info("");
        return true;
    }

    private File getDestination(Context context, String path) {
        Path home = FileSystems.getDefault().getPath(context.getSubmissionDirectory()
                .getAbsolutePath());
        return home.resolve(context.replaceProperties(path)).toFile().getAbsoluteFile();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Plugin.Builder<CopyPlugin,Builder> {

        private String file;
        private String toFile;
        private String toDirectory;

        private Builder() {
        }

        public Builder setFile(String file) {
            this.file = file;
            return this;
        }

        public Builder setToFile(String toFile) {
            this.toFile = toFile;
            return this;
        }

        public Builder setToDirectory(String toDirectory) {
            this.toDirectory = toDirectory;
            return this;
        }

        @Override
        protected Builder getThisObj() {
            return this;
        }

        @Override
        public CopyPlugin build() {
            return new CopyPlugin(this);
        }
    }
}
