package org.lakunu.labs.config;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.lakunu.labs.DefaultLabBuilder;
import org.lakunu.labs.Lab;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;

public class JsonLabFactoryTest {

    @Test
    public void testAnonymousLab() throws Exception {
        String input = "{\n" +
                "  \"build\": [\n" +
                "    {\n" +
                "      \"plugin\": \"ant\",\n" +
                "      \"target\": \"jar\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"run\": [\n" +
                "    {\n" +
                "      \"plugin\": \"run-command\",\n" +
                "      \"command\": \"java\",\n" +
                "      \"args\": [\"-jar\", \"build/rational.jar\", \"1\", \"2\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Lab lab = JsonLabFactory.newLab(in);
        Assert.assertEquals("anonymous", lab.getName());
        Assert.assertThat(lab.getPhases(), is(DefaultLabBuilder.PHASE_ORDER));
        Assert.assertThat(lab.getActivePhases(), is(ImmutableList.of("build", "run")));
    }

    @Test
    public void testNamedLab() throws Exception {
        String input = "{\n" +
                "  \"_name\": \"foo\"," +
                "  \"build\": [\n" +
                "    {\n" +
                "      \"plugin\": \"ant\",\n" +
                "      \"target\": \"jar\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"run\": [\n" +
                "    {\n" +
                "      \"plugin\": \"run-command\",\n" +
                "      \"command\": \"java\",\n" +
                "      \"args\": [\"-jar\", \"build/rational.jar\", \"1\", \"2\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Lab lab = JsonLabFactory.newLab(in);
        Assert.assertEquals("foo", lab.getName());
        Assert.assertThat(lab.getPhases(), is(DefaultLabBuilder.PHASE_ORDER));
        Assert.assertThat(lab.getActivePhases(), is(ImmutableList.of("build", "run")));
    }

    @Test
    public void testInvalidBuilder() throws Exception {
        String input = "{\n" +
                "  \"_builder\": \"Bogus_Type\"," +
                "  \"build\": [\n" +
                "    {\n" +
                "      \"plugin\": \"ant\",\n" +
                "      \"target\": \"jar\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"run\": [\n" +
                "    {\n" +
                "      \"plugin\": \"run-command\",\n" +
                "      \"command\": \"java\",\n" +
                "      \"args\": [\"-jar\", \"build/rational.jar\", \"1\", \"2\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        try {
            JsonLabFactory.newLab(in);
            Assert.fail("No error thrown for invalid lab builder");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testInvalidPlugin() throws Exception {
        String input = "{\n" +
                "  \"build\": [\n" +
                "    {\n" +
                "      \"plugin\": \"ant\",\n" +
                "      \"target\": \"jar\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"run\": [\n" +
                "    {\n" +
                "      \"plugin\": \"bogus-plugin\",\n" +
                "      \"command\": \"java\",\n" +
                "      \"args\": [\"-jar\", \"build/rational.jar\", \"1\", \"2\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        try {
            JsonLabFactory.newLab(in);
            Assert.fail("No error thrown for invalid plugin");
        } catch (Exception ignored) {
        }
    }

}
