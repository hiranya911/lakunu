package org.lakunu.labs;

import java.util.Comparator;

/**
 * Controls the order of phase execution.
 */
public interface Lifecycle {

    boolean isSupported(String phase);

    Comparator<Phase> comparator();

}
