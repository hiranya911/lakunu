package org.lakunu.labs.submit;

import org.apache.commons.io.FileUtils;
import org.lakunu.labs.Evaluation;

import java.io.File;

public class TestSubmission extends Submission {

    @Override
    public File prepare(Evaluation.Context context) {
        return FileUtils.getTempDirectory();
    }
}
