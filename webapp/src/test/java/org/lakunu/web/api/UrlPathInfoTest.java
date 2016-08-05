package org.lakunu.web.api;

import org.junit.Assert;
import org.junit.Test;

public class UrlPathInfoTest {

    @Test
    public void testEmptyPath() {
        UrlPathInfo pathInfo = new UrlPathInfo((String) null);
        Assert.assertTrue(pathInfo.isEmpty());
        try {
            pathInfo.get(0);
            Assert.fail("No error thrown for empty path");
        } catch (IndexOutOfBoundsException ignored) {
        }
        pathInfo = new UrlPathInfo("/");
        Assert.assertTrue(pathInfo.isEmpty());
        try {
            pathInfo.get(0);
            Assert.fail("No error thrown for empty path");
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void testSimplePath() throws Exception {
        UrlPathInfo pathInfo = new UrlPathInfo("/course");
        Assert.assertFalse(pathInfo.isEmpty());
        Assert.assertEquals("course", pathInfo.get(0));
        try {
            pathInfo.get(1);
            Assert.fail("No error thrown");
        } catch (IndexOutOfBoundsException ignored) {
        }

        pathInfo = new UrlPathInfo("/course/");
        Assert.assertFalse(pathInfo.isEmpty());
        Assert.assertEquals("course", pathInfo.get(0));
        try {
            pathInfo.get(1);
            Assert.fail("No error thrown");
        } catch (IndexOutOfBoundsException ignored) {
        }

        pathInfo = new UrlPathInfo("/course/1");
        Assert.assertFalse(pathInfo.isEmpty());
        Assert.assertEquals("course", pathInfo.get(0));
        Assert.assertEquals("1", pathInfo.get(1));
    }

}
