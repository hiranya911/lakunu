package org.lakunu.labs.plugins;

import junit.framework.Assert;
import org.junit.Test;

public class PluginTest {

    @Test
    public void testFailOnError() {
        CustomTestPlugin plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> true)
                .build();
        Assert.assertTrue(plugin.execute(null));

        plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> false)
                .build();
        Assert.assertFalse(plugin.execute(null));

        plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> false)
                .setFailOnError(false)
                .build();
        Assert.assertTrue(plugin.execute(null));
    }

    @Test
    public void testFailOnErrorWithException() {
        CustomTestPlugin plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> {
                    throw new RuntimeException("expected error");
                })
                .build();
        try {
            plugin.execute(null);
            Assert.fail("No exception thrown");
        } catch (Exception expected) {
        }

        plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> {
                    throw new RuntimeException("expected error");
                })
                .setFailOnError(false)
                .build();
        Assert.assertTrue(plugin.execute(null));
    }

}
