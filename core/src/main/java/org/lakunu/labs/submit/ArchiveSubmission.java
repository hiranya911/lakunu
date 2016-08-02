package org.lakunu.labs.submit;

import org.apache.commons.io.FileUtils;
import org.lakunu.labs.Evaluation;
import org.lakunu.labs.resources.ArchiveFile;

import java.io.File;
import java.io.IOException;

public final class ArchiveSubmission extends Submission {

    private final ArchiveFile archiveFile;

    public ArchiveSubmission(File archiveFile) {
        this.archiveFile = ArchiveFile.newArchiveFile(archiveFile.getAbsolutePath());
    }

    @Override
    public File prepare(Evaluation.Context context) throws IOException {
        File tempDir = new File(context.getEvaluationDirectory(), "_submission");
        FileUtils.forceMkdir(tempDir);
        archiveFile.extract(tempDir);
        File[] entries = tempDir.listFiles();
        if (entries != null && entries.length == 1 && entries[0].isDirectory()) {
            return entries[0];
        } else {
            return tempDir;
        }
    }
}
