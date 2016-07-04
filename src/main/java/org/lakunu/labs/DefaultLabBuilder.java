package org.lakunu.labs;

import com.google.common.collect.ImmutableList;

public class DefaultLabBuilder extends Lab.Builder {

    public static final String BUILD_PHASE = "build";
    public static final String RUN_PHASE = "run";

    public static final ImmutableList<String> PHASE_ORDER = ImmutableList.of(BUILD_PHASE, RUN_PHASE);

    private DefaultLabBuilder() {
    }

    @Override
    protected ImmutableList<String> getPhases() {
        return PHASE_ORDER;
    }

    public static DefaultLabBuilder newBuilder() {
        return new DefaultLabBuilder();
    }
}
