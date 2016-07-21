package org.lakunu.labs.resources;

import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class ResourcesTest {

    @Test
    public void testEmptyResources() {
        Resources r = new Resources(ImmutableSet.of());
        Assert.assertNull(r.lookup("foo", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonExistingFiles() {
        new Resources(ImmutableSet.of(new File("bogus_as_hell.txt")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectoryAsFile() {
        File dir = new File("src/test/resources");
        Assert.assertTrue(dir.exists());
        new Resources(ImmutableSet.of(dir));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateFiles() {
        File sample1 = new File("src/test/resources/sample1.xml");
        File sample2 = new File("src/test/resources/samples/sample1.xml");
        Assert.assertTrue(sample1.exists() && sample1.isFile());
        Assert.assertTrue(sample2.exists() && sample2.isFile());
        new Resources(ImmutableSet.of(sample1, sample2));
    }

    @Test
    public void testFiles() {
        final File sample1 = new File("src/test/resources/sample1.xml");
        final File sample2 = new File("src/test/resources/sample2.xml");
        Resources r = new Resources(ImmutableSet.of(sample1, sample2));
        File f = r.lookup("sample1.xml", null);
        Assert.assertNotNull(f);
        Assert.assertEquals(sample1, f);

        f = r.lookup("sample2.xml", null);
        Assert.assertNotNull(f);
        Assert.assertEquals(sample2, f);
    }

    @Test(expected = NullPointerException.class)
    public void testNullCollection() {
        new Resources((ResourceCollection) null);
    }

    @Test
    public void testBasicCollection() {
        Resources r = new Resources((name, context) -> {
            if (name.equals("foo")) {
                return new File("foo");
            } else {
                return null;
            }
        });
        Assert.assertEquals(new File("foo"), r.lookup("foo", null));
        Assert.assertNull(r.lookup("bar", null));
    }

}
