package org.lakunu.labs.plugins;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.lakunu.labs.Evaluation;
import org.lakunu.labs.EvaluationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testStringReplacement() {
        EvaluationTest.TestContext testContext = EvaluationTest.testContextBuilder()
                .addProperty("p1", "foo")
                .addProperty("p2", "bar")
                .build();
        List<String> values = new ArrayList<>();
        CustomTestPlugin plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> {
                    values.add(context.replaceProperties("* ${p1} * ${p2} * ${p3}"));
                    return true;
                })
                .build();
        Assert.assertTrue(plugin.execute(testContext));
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("* foo * bar * ${p3}", values.get(0));
    }

    @Test
    public void testResourceRefNoResourceDir() {
        EvaluationTest.TestContext testContext = EvaluationTest.testContextBuilder().build();
        CustomTestPlugin plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> {
                    context.replaceProperties("* ${res:foo} *");
                    return true;
                })
                .build();
        try {
            plugin.execute(testContext);
            Assert.fail("No error thrown for missing resources directory");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testResourceRefNoResourceFile() {
        EvaluationTest.TestContext testContext = EvaluationTest.testContextBuilder()
                .setResourceDirectory(FileUtils.getTempDirectory())
                .build();
        CustomTestPlugin plugin = CustomTestPlugin.newBuilder()
                .setFunction(context -> {
                    context.replaceProperties("* ${res:foo} *");
                    return true;
                })
                .build();
        try {
            plugin.execute(testContext);
            Assert.fail("No error thrown for missing resources directory");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testResourceDirReplacement() throws Exception {
        File tempFile = File.createTempFile("lakunu", null, FileUtils.getTempDirectory());
        try {
            EvaluationTest.TestContext testContext = EvaluationTest.testContextBuilder()
                    .setResourceDirectory(FileUtils.getTempDirectory())
                    .build();
            List<String> values = new ArrayList<>();
            CustomTestPlugin plugin = CustomTestPlugin.newBuilder()
                    .setFunction(context -> {
                        values.add(context.replaceProperties("* ${res:" + tempFile.getName() + "} *"));
                        return true;
                    })
                    .build();
            Assert.assertTrue(plugin.execute(testContext));
            Assert.assertEquals(1, values.size());
            Assert.assertEquals("* " + tempFile.getAbsolutePath() + " *", values.get(0));
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    public static Plugin.Context pluginContext(Evaluation.Context evalContext) {
        return new Plugin.Context(evalContext);
    }

}
