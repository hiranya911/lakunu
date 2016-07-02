package org.lakunu.labs;

import java.io.File;

public interface Plugin {

    default boolean execute(File currentDir) {
        return false;
    }

}
