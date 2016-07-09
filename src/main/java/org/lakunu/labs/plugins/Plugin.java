package org.lakunu.labs.plugins;

import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Evaluation;
import org.lakunu.labs.LabOutputHandler;
import org.lakunu.labs.Score;
import org.lakunu.labs.plugins.validators.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class Plugin {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final boolean failOnError;
    private final ImmutableList<Validator> preValidators;
    private final ImmutableList<Validator> postValidators;

    protected Plugin(Builder<?,?> builder) {
        this.failOnError = builder.failOnError;
        this.preValidators = ImmutableList.copyOf(builder.preValidators);
        this.postValidators = ImmutableList.copyOf(builder.postValidators);
    }

    public final boolean execute(Evaluation.Context context) {
        Context pluginContext = new Context(context);
        preValidators.forEach(v -> context.addScore(v.validate(pluginContext)));
        try {
            boolean success = doExecute(pluginContext);
            pluginContext.success = success;
            postValidators.forEach(v -> context.addScore(v.validate(pluginContext)));
            return success || !failOnError;
        } catch (Exception e) {
            if (failOnError) {
                throw new RuntimeException(e);
            } else {
                logger.warn("Error while executing plugin", e);
                return true;
            }
        }
    }

    public final ImmutableList<Score> getRubric() {
        ImmutableList.Builder<Score> rubric = ImmutableList.builder();
        preValidators.forEach(v -> rubric.add(v.zero()));
        postValidators.forEach(v -> rubric.add(v.zero()));
        return rubric.build();
    }

    protected abstract boolean doExecute(Context context) throws Exception;

    public final static class Context {

        private final Evaluation.Context context;
        private final Path submissionPath;
        private String output;
        private String errors;
        private boolean success;

        private final Map<String,Object> properties = new HashMap<>();

        Context(Evaluation.Context context) {
            this.context = context;
            this.submissionPath = FileSystems.getDefault().getPath(context.getSubmissionDirectory()
                    .getAbsolutePath());
        }

        public LabOutputHandler getOutputHandler() {
            return context.getOutputHandler();
        }

        public File getSubmissionDirectory() {
            return context.getSubmissionDirectory();
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        public String getErrors() {
            return errors;
        }

        public void setErrors(String errors) {
            this.errors = errors;
        }

        public File resolvePath(String path) {
            return submissionPath.resolve(path).toFile().getAbsoluteFile();
        }

        public File getResource(String name) {
            File resourcesDirectory = context.getResourcesDirectory();
            checkArgument(resourcesDirectory != null, "resources directory unavailable");
            Path resource = resourcesDirectory.toPath().resolve(name).toAbsolutePath();
            checkArgument(Files.exists(resource), "resource: {} does not exist", resource.toString());
            return resource.toFile();
        }

        public boolean isSuccess() {
            return success;
        }

        public final <T> T getProperty(String name, Class<T> clazz) {
            Object value =  properties.get(name);
            if (value != null) {
                return clazz.cast(value);
            }
            return null;
        }

        public final void setProperty(String name, Object value) {
            properties.put(name, value);
        }
    }

    public static abstract class Builder<T extends Plugin,B extends Builder<T,B>> {
        private final B thisObj;

        private boolean failOnError = true;
        private final List<Validator> preValidators = new ArrayList<>();
        private final List<Validator> postValidators = new ArrayList<>();

        protected Builder() {
            this.thisObj = getThisObj();
        }

        public final B setFailOnError(boolean failOnError) {
            this.failOnError = failOnError;
            return thisObj;
        }

        public final B addPreValidator(Validator validator) {
            this.preValidators.add(validator);
            return thisObj;
        }

        public final B addPostValidator(Validator validator) {
            this.postValidators.add(validator);
            return thisObj;
        }

        protected abstract B getThisObj();
        public abstract T build();
    }

}
