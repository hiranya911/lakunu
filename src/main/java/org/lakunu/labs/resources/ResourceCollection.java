package org.lakunu.labs.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public abstract class ResourceCollection {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public abstract void copyTo(File evaluationDir) throws IOException;

}
