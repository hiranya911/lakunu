package org.lakunu.labs.plugins;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.lakunu.labs.Evaluation;
import org.lakunu.labs.EvaluationTest;
import org.lakunu.labs.TestOutputHandler;

import java.io.File;

public class CopyPluginTest {

    private Plugin.Context getPluginContext() {
        Evaluation.Context evalContext = EvaluationTest.testContextBuilder()
                .setOutputHandler(new TestOutputHandler())
                .build();
        return PluginTest.pluginContext(evalContext);
    }

    @Test
    public void testFileToFileCopy() throws Exception {
        File srcFile = File.createTempFile("lakunu", null, FileUtils.getTempDirectory());
        File destFile = new File(FileUtils.getTempDirectory(), "copy_" + srcFile.getName());
        try {
            CopyPlugin plugin = CopyPlugin.newBuilder()
                    .setFile(srcFile.getAbsolutePath())
                    .setToFile(destFile.getAbsolutePath())
                    .build();
            checkCopyOperation(srcFile, destFile, plugin);
        } finally {
            FileUtils.deleteQuietly(srcFile);
            FileUtils.deleteQuietly(destFile);
        }
    }

    @Test
    public void testFileToDirCopy() throws Exception {

        File srcFile = File.createTempFile("lakunu", null, FileUtils.getTempDirectory());
        File destDir = new File(FileUtils.getTempDirectory(), RandomStringUtils.randomAlphanumeric(32));
        File destFile = new File(destDir, srcFile.getName());
        try {
            CopyPlugin plugin = CopyPlugin.newBuilder()
                    .setFile(srcFile.getAbsolutePath())
                    .setToDirectory(destDir.getAbsolutePath())
                    .build();
            checkCopyOperation(srcFile, destFile, plugin);
        } finally {
            FileUtils.deleteQuietly(srcFile);
            FileUtils.deleteQuietly(destDir);
        }
    }

    @Test
    public void testDirToFileCopy() throws Exception {
        File srcDir = new File(FileUtils.getTempDirectory(), RandomStringUtils.randomAlphanumeric(32));
        FileUtils.forceMkdir(srcDir);
        File srcFile = File.createTempFile("lakunu", null, srcDir);
        File destDir = new File(FileUtils.getTempDirectory(), RandomStringUtils.randomAlphanumeric(32));
        File destFile = new File(destDir, srcFile.getName());
        try {
            CopyPlugin plugin = CopyPlugin.newBuilder()
                    .setFile(srcDir.getAbsolutePath())
                    .setToFile(destDir.getAbsolutePath())
                    .build();
            checkCopyOperation(srcFile, destFile, plugin);
        } finally {
            FileUtils.deleteQuietly(srcDir);
            FileUtils.deleteQuietly(destDir);
        }
    }

    @Test
    public void testDirToDirCopy() throws Exception {
        File srcDir = new File(FileUtils.getTempDirectory(), RandomStringUtils.randomAlphanumeric(32));
        FileUtils.forceMkdir(srcDir);
        File srcFile = File.createTempFile("lakunu", null, srcDir);
        File destDir = new File(FileUtils.getTempDirectory(), RandomStringUtils.randomAlphanumeric(32));
        FileUtils.forceMkdir(destDir);
        File destFile = FileUtils.getFile(destDir, srcDir.getName(), srcFile.getName());
        try {
            CopyPlugin plugin = CopyPlugin.newBuilder()
                    .setFile(srcDir.getAbsolutePath())
                    .setToDirectory(destDir.getAbsolutePath())
                    .build();
            checkCopyOperation(srcFile, destFile, plugin);
        } finally {
            FileUtils.deleteQuietly(srcDir);
            FileUtils.deleteQuietly(destDir);
        }
    }

    private void checkCopyOperation(File srcFile, File destFile,
                                    CopyPlugin plugin) throws Exception {
        Assert.assertTrue(srcFile.exists());
        Assert.assertFalse(destFile.exists());
        Assert.assertTrue(plugin.doExecute(getPluginContext()));
        Assert.assertTrue(srcFile.exists());
        Assert.assertTrue(destFile.exists());
    }

}
