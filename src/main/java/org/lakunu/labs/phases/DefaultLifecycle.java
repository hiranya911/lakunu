package org.lakunu.labs.phases;

import com.google.common.collect.ImmutableMap;
import org.lakunu.labs.Lifecycle;
import org.lakunu.labs.Phase;

import java.util.*;

public final class DefaultLifecycle implements Lifecycle {

    private static final ImmutableMap<String, Integer> PHASE_ORDER = ImmutableMap.of(
            BuildPhase.NAME, 100,
            RunPhase.NAME, 200
    );

    public static final DefaultLifecycle INSTANCE = new DefaultLifecycle();

    private DefaultLifecycle() {
    }

    @Override
    public boolean isSupported(String phase) {
        return PHASE_ORDER.containsKey(phase);
    }

    @Override
    public Comparator<Phase> comparator() {
        return (p1,p2) -> PHASE_ORDER.get(p1.getName()) - PHASE_ORDER.get(p2.getName());
    }
}
