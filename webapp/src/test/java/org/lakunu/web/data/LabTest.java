package org.lakunu.web.data;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;

public class LabTest {

    @Test
    public void testPermissions() {
        Assert.assertEquals("lab:op:courseId:labId", Lab.permission("op", "courseId", "labId"));
        Assert.assertEquals("lab:*:courseId:*", Lab.permission("*", "courseId", "*"));
        Assert.assertEquals("lab:add:cs56:*", LabDAO.ADD_PERMISSION("cs56"));
        Assert.assertEquals("lab:get:cs56:lab00", LabDAO.GET_PERMISSION("cs56", "lab00"));

        Lab lab = Lab.newBuilder()
                .setId("lab00")
                .setCourseId("cs56")
                .setName("test")
                .setDescription("test")
                .setCreatedBy("foo")
                .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
        Assert.assertEquals("lab:update:cs56:lab00", LabDAO.UPDATE_PERMISSION(lab));
    }

}
