package org.lakunu.labs.resources;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

public class DirectoryResourceCollectionTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNonDirectory() {
        new DirectoryResourceCollection("src/test/resources/sample1.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonExistingDirectory() {
        new DirectoryResourceCollection("bogus/as/hell");
    }

    @Test
    public void testDirectory() {
        DirectoryResourceCollection dir = new DirectoryResourceCollection("src/test/resources");
        File file = dir.lookup("sample1.xml", null);
        Assert.assertEquals(new File("src/test/resources/sample1.xml").getAbsoluteFile(), file);

        file = dir.lookup("samples/sample1.xml", null);
        Assert.assertEquals(new File("src/test/resources/samples/sample1.xml").getAbsoluteFile(), file);

        file = dir.lookup(".", null);
        Assert.assertEquals(new File("src/test/resources/.").getAbsoluteFile(), file);

        file = dir.lookup("", null);
        Assert.assertEquals(new File("src/test/resources").getAbsoluteFile(), file);

        file = dir.lookup("bogus_as_hell.txt", null);
        Assert.assertNull(file);
    }

}
