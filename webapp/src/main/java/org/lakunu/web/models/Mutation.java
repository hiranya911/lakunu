package org.lakunu.web.models;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Mutation<T> {

    protected final T original;

    protected Mutation(T original) {
        checkNotNull(original, "Original is required");
        this.original = original;
    }

    public abstract T apply();

}
