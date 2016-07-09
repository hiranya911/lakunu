package org.lakunu.labs.resources;

import java.io.File;
import java.io.IOException;

public abstract class ResourceCollection {

    public abstract void copyTo(File evaluationDir) throws IOException;

}
