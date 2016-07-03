package org.lakunu.labs;

public interface Plugin {

    default boolean execute(LabContext context) {
        return false;
    }

}
