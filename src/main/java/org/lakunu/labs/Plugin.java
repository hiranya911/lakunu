package org.lakunu.labs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Plugin {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final boolean failOnError;

    public Plugin(Builder builder) {
        this.failOnError = builder.failOnError;
    }

    public final boolean execute(LabContext context) {
        try {
            return doExecute(context);
        } catch (Exception e) {
            if (failOnError) {
                throw new RuntimeException(e);
            } else {
                logger.warn("Error while executing plugin", e);
                return true;
            }
        }
    }

    protected abstract boolean doExecute(LabContext context) throws Exception;

    public static abstract class Builder<T extends Plugin,B extends Builder<T,B>> {
        private final B thisObj;

        private boolean failOnError = true;

        protected Builder() {
            this.thisObj = getThisObj();
        }

        public final B setFailOnError(boolean failOnError) {
            this.failOnError = failOnError;
            return thisObj;
        }

        protected abstract B getThisObj();
        public abstract T build();
    }

}
