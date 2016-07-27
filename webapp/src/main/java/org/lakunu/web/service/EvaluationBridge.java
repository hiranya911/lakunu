package org.lakunu.web.service;

import org.lakunu.web.models.Lab;

public interface EvaluationBridge {

    boolean validate(Lab lab) throws Exception;

}
