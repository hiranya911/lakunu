package org.lakunu.labs.plugins;

import junit.framework.Assert;
import org.junit.Test;
import org.lakunu.labs.Evaluation;
import org.lakunu.labs.EvaluationTest;

public class PluginTest {

    @Test
    public void testFailOnError() {
        EvaluationTest.TestContext testContext = EvaluationTest.testContextBuilder().build();
        CustomTestPlugin plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> true)
                .build();
        Assert.assertTrue(plugin.execute(testContext));

        plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> false)
                .build();
        Assert.assertFalse(plugin.execute(testContext));

        plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> false)
                .setFailOnError(false)
                .build();
        Assert.assertTrue(plugin.execute(testContext));
    }

    @Test
    public void testFailOnErrorWithException() {
        EvaluationTest.TestContext testContext = EvaluationTest.testContextBuilder().build();
        CustomTestPlugin plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> {
                    throw new RuntimeException("expected error");
                })
                .build();
        try {
            plugin.execute(testContext);
            Assert.fail("No exception thrown");
        } catch (Exception ignored) {
        }

        plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> {
                    throw new RuntimeException("expected error");
                })
                .setFailOnError(false)
                .build();
        Assert.assertTrue(plugin.execute(testContext));
    }

}
