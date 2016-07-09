package org.lakunu.labs.plugins;

import com.google.common.base.Strings;
import com.google.common.primitives.Booleans;
import org.apache.commons.io.FileUtils;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;

public final class CopyPlugin extends Plugin {

    private final File file;
    private final String resource;
    private final String toFile;
    private final String toDirectory;

    private CopyPlugin(Builder builder) {
        super(builder);
        int sources = Booleans.countTrue(!Strings.isNullOrEmpty(builder.file),
                !Strings.isNullOrEmpty(builder.resource));
        checkArgument(sources == 1, "Exactly one of file or resource is required");
        int targets = Booleans.countTrue(!Strings.isNullOrEmpty(builder.toFile),
                !Strings.isNullOrEmpty(builder.toDirectory));
        checkArgument(targets == 1, "Exactly one of toFile or toDirectory is required");
        if (builder.file != null) {
            this.file = new File(builder.file).getAbsoluteFile();
            checkArgument(this.file.exists(), "File: {} does not exist", this.file.getPath());
        } else {
            this.file = null;
        }
        this.resource = builder.resource;
        this.toFile = builder.toFile;
        this.toDirectory = builder.toDirectory;
    }

    @Override
    protected boolean doExecute(Context context) throws Exception {
        File source = getSource(context);
        if (toFile != null) {
            File dest = context.resolvePath(toFile);
            if (source.isDirectory()) {
                FileUtils.copyDirectory(source, dest);
            } else {
                FileUtils.copyFile(source, dest);
            }
            context.getOutputHandler().info("Copied " + source.getAbsolutePath() + " as " +
                    dest.getAbsolutePath());
        } else {
            File dest = context.resolvePath(toDirectory);
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

    private File getSource(Context context) {
        if (file != null) {
            return file;
        } else {
            return context.getResource(resource);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Plugin.Builder<CopyPlugin,Builder> {

        private String file;
        private String resource;
        private String toFile;
        private String toDirectory;

        private Builder() {
        }

        public Builder setFile(String file) {
            this.file = file;
            return this;
        }

        public Builder setResource(String resource) {
            this.resource = resource;
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
