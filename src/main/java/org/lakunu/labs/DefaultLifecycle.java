package org.lakunu.labs;

import com.google.common.collect.ImmutableList;

public final class DefaultLifecycle extends Lifecycle.Builder {

    public static final String NAME = "default";

    public static final String BUILD_PHASE = "build";
    public static final String RUN_PHASE = "run";

    public static final ImmutableList<String> PHASE_ORDER = ImmutableList.of(BUILD_PHASE, RUN_PHASE);

    public DefaultLifecycle() {
        super(PHASE_ORDER);
    }

}
