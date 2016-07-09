package org.lakunu.labs.plugins.validators;

import com.google.common.collect.ImmutableMap;

import java.util.List;

public final class FileExistsValidatorFactory extends ValidatorFactory<FileExistsValidator> {

    @Override
    public String getName() {
        return "file-exists";
    }

    @Override
    public FileExistsValidator build(ImmutableMap<String, Object> properties) {
        FileExistsValidator.Builder builder = FileExistsValidator.newBuilder()
                .setName(getProperty(properties, "name", String.class))
                .setScore(getNumericProperty(properties, "score"))
                .setScorePerFile(getNumericProperty(properties, "scorePerFile"))
                .setTestNotExists(getProperty(properties, "testNotExists", false, Boolean.class));
        List<?> files = getProperty(properties, "files", List.class);
        if (files != null) {
            files.forEach(f -> builder.addFile(f.toString()));
        }
        return builder.build();
    }
}
