package org.lakunu.labs.submit;

import java.io.File;

import static com.google.common.base.Preconditions.checkState;

public abstract class Submission {

    private boolean prepared;

    public final void prepare(File targetDir) {
        checkState(!prepared, "Submission is already prepared");
        doPrepare(targetDir);
        prepared = true;
    }

    public final File getDirectory() {
        checkState(prepared, "Submission is not prepared");
        return doGetDirectory();
    }

    protected void doPrepare(File targetDir) {

    }

    protected abstract File doGetDirectory();

}
