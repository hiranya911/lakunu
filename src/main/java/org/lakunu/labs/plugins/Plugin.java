package org.lakunu.labs.plugins;

import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Evaluation;
import org.lakunu.labs.LabOutputHandler;
import org.lakunu.labs.plugins.validators.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Plugin {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final boolean failOnError;
    private final ImmutableList<Validator> validators;

    protected Plugin(Builder<?,?> builder) {
        this.failOnError = builder.failOnError;
        this.validators = ImmutableList.copyOf(builder.validators);
    }

    public final boolean execute(Evaluation.Context context) {
        Context pluginContext = new Context(context);
        try {
            boolean success = doExecute(pluginContext);
            pluginContext.success = success;
            return success || !failOnError;
        } catch (Exception e) {
            pluginContext.exception = e;
            if (failOnError) {
                throw new RuntimeException(e);
            } else {
                logger.warn("Error while executing plugin", e);
                return true;
            }
        } finally {
            validators.forEach(v -> context.addScore(v.validate(pluginContext)));
        }
    }

    protected abstract boolean doExecute(Context context) throws Exception;

    public final static class Context {

        private final LabOutputHandler outputHandler;
        private final File submissionDirectory;
        private String output;
        private String errors;
        private Exception exception;
        private boolean success;

        private Context(Evaluation.Context context) {
            this.outputHandler = context.getOutputHandler();
            this.submissionDirectory = context.getSubmissionDirectory();
        }

        public LabOutputHandler getOutputHandler() {
            return outputHandler;
        }

        public File getSubmissionDirectory() {
            return submissionDirectory;
        }

        public String getOutput() {
            return output;
        }

        public Context setOutput(String output) {
            this.output = output;
            return this;
        }

        public String getErrors() {
            return errors;
        }

        public Context setErrors(String errors) {
            this.errors = errors;
            return this;
        }

        public Exception getException() {
            return exception;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public static abstract class Builder<T extends Plugin,B extends Builder<T,B>> {
        private final B thisObj;

        private boolean failOnError = true;
        private final List<Validator> validators = new ArrayList<>();

        protected Builder() {
            this.thisObj = getThisObj();
        }

        public final B setFailOnError(boolean failOnError) {
            this.failOnError = failOnError;
            return thisObj;
        }

        public final B addValidator(Validator validator) {
            this.validators.add(validator);
            return thisObj;
        }

        protected abstract B getThisObj();
        public abstract T build();
    }

}
