package org.lakunu.labs.plugins;

import junit.framework.Assert;
import org.junit.Test;
import org.lakunu.labs.Evaluation;

public class PluginTest {

    @Test
    public void testFailOnError() {
        CustomTestPlugin plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> true)
                .build();
        Assert.assertTrue(plugin.execute(Evaluation.newTestContext()));

        plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> false)
                .build();
        Assert.assertFalse(plugin.execute(Evaluation.newTestContext()));

        plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> false)
                .setFailOnError(false)
                .build();
        Assert.assertTrue(plugin.execute(Evaluation.newTestContext()));
    }

    @Test
    public void testFailOnErrorWithException() {
        CustomTestPlugin plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> {
                    throw new RuntimeException("expected error");
                })
                .build();
        try {
            plugin.execute(Evaluation.newTestContext());
            Assert.fail("No exception thrown");
        } catch (Exception ignored) {
        }

        plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> {
                    throw new RuntimeException("expected error");
                })
                .setFailOnError(false)
                .build();
        Assert.assertTrue(plugin.execute(Evaluation.newTestContext()));
    }

}
