package org.lakunu.web.service.permissions;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class Permission {

    protected final String model;
    protected final String operation;

    Permission(String model, String operation) {
        checkArgument(!Strings.isNullOrEmpty(model), "Model is required");
        checkArgument(!Strings.isNullOrEmpty(operation), "Operation is required");
        this.model = model;
        this.operation = operation;
    }

    protected abstract String getIdentifier();

    @Override
    public final String toString() {
        return model + ":" + operation + ":" + getIdentifier();
    }

    public static Permission aggregate(Permission... permissions) {
        Set<String> models = new LinkedHashSet<>();
        Set<String> operations = new LinkedHashSet<>();
        Set<String> identifiers = new LinkedHashSet<>();
        for (Permission p : permissions) {
            models.add(p.model);
            operations.add(p.operation);
            identifiers.add(p.getIdentifier());
        }

        final String identifier = Iterables.getOnlyElement(identifiers, "*");
        return new Permission(Iterables.getOnlyElement(models), String.join(",", operations)) {
            @Override
            protected String getIdentifier() {
                return identifier;
            }
        };
    }
}
