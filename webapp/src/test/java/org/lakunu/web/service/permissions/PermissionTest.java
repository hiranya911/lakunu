package org.lakunu.web.service.permissions;

import org.junit.Assert;
import org.junit.Test;

public class PermissionTest {

    @Test
    public void testPermissionToString() {
        Permission p = new Permission("model", "operation") {
            @Override
            protected String getIdentifier() {
                return "id1";
            }
        };
        Assert.assertEquals("model:operation:id1", p.toString());

        p = new Permission("model", "operation") {
            @Override
            protected String getIdentifier() {
                return "id1:id2";
            }
        };
        Assert.assertEquals("model:operation:id1:id2", p.toString());
    }

    @Test
    public void testPermissionAggregation() {
        Permission p1 = new Permission("model", "op1") {
            @Override
            protected String getIdentifier() {
                return "*";
            }
        };
        Permission p2 = new Permission("model", "op2") {
            @Override
            protected String getIdentifier() {
                return "*";
            }
        };
        Permission p3 = new Permission("model", "op3") {
            @Override
            protected String getIdentifier() {
                return "*";
            }
        };
        Assert.assertEquals("model:op1,op2,op3:*", Permission.aggregate(p1, p2, p3).toString());
    }

}
