package org.lakunu.labs;

import org.junit.Test;
import org.lakunu.labs.plugins.TestPlugin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DefaultEvaluationConfigTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNoPlugins() {
        EvaluationConfig.newBuilder(DefaultEvaluationConfig.NAME).build();
    }

    @Test
    public void testPhaseOrder() {
        EvaluationConfig evaluationConfig = EvaluationConfig.newBuilder(DefaultEvaluationConfig.NAME)
                .addPlugin(DefaultEvaluationConfig.BUILD_PHASE, TestPlugin.newInstance())
                .build();
        assertThat(evaluationConfig.getPhaseOrder(), is(DefaultEvaluationConfig.PHASE_ORDER));

        evaluationConfig = EvaluationConfig.newBuilder(DefaultEvaluationConfig.NAME)
                .addPlugin(DefaultEvaluationConfig.BUILD_PHASE, TestPlugin.newInstance())
                .addPlugin(DefaultEvaluationConfig.RUN_PHASE, TestPlugin.newInstance())
                .build();
        assertThat(evaluationConfig.getPhaseOrder(), is(DefaultEvaluationConfig.PHASE_ORDER));

        evaluationConfig = EvaluationConfig.newBuilder(DefaultEvaluationConfig.NAME)
                .addPlugin(DefaultEvaluationConfig.RUN_PHASE, TestPlugin.newInstance())
                .addPlugin(DefaultEvaluationConfig.BUILD_PHASE, TestPlugin.newInstance())
                .build();
        assertThat(evaluationConfig.getPhaseOrder(), is(DefaultEvaluationConfig.PHASE_ORDER));
    }

}
