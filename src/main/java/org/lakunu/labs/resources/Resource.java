package org.lakunu.labs.resources;

import java.io.File;
import java.io.IOException;

public abstract class Resource {

    public abstract void copyTo(File resourcesDir) throws IOException;

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

}
