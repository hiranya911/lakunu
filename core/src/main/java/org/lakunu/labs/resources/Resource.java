package org.lakunu.labs.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public abstract class Resource {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public abstract void copyTo(File resourcesDir) throws IOException;

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

}
