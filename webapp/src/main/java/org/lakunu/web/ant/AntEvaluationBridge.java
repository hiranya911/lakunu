package org.lakunu.web.ant;

import org.apache.commons.io.FileUtils;
import org.lakunu.labs.ant.AntEvaluationPlan;
import org.lakunu.web.models.Lab;
import org.lakunu.web.service.EvaluationBridge;

import java.io.File;

public final class AntEvaluationBridge implements EvaluationBridge {

    @Override
    public boolean validate(Lab lab) throws Exception {
        File tempFile = File.createTempFile("lakunu_web_", null);
        try {
            FileUtils.writeByteArrayToFile(tempFile, lab.getConfiguration());
            AntEvaluationPlan evaluationPlan = new AntEvaluationPlan(tempFile, null);
            org.lakunu.labs.Lab.newBuilder()
                    .setName(lab.getName())
                    .setEvaluationPlan(evaluationPlan)
                    .build();
            return true;
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }
}
