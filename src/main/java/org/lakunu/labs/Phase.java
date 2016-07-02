package org.lakunu.labs;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class Phase {

    protected final String name;

    public Phase(String name) {
        checkArgument(!Strings.isNullOrEmpty(name), "Name is required");
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    @Override
    public final boolean equals(Object obj) {
        return obj != null && obj instanceof Phase && name.equals(((Phase) obj).name);
    }

    @Override
    public final String toString() {
        return name;
    }
}
