package org.lakunu.labs.resources;

import org.lakunu.labs.Evaluation;

import java.io.File;

public interface ResourceCollection {

    File lookup(String name, Evaluation.Context context);

}
