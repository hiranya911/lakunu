package org.lakunu.labs.resources;

import com.google.common.collect.ImmutableSet;
import org.lakunu.labs.Evaluation;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Resources {

    private final ImmutableSet<File> files;
    private final ResourceCollection collection;

    public Resources(Set<File> files) {
        checkNotNull(files, "File set cannot be null");
        Set<String> fileNames = new HashSet<>();
        files.forEach(f -> {
            checkArgument(f.exists() && f.isFile(),
                    "%s does not exist or is not a regular file", f.getAbsolutePath());
            checkArgument(fileNames.add(f.getName()), "duplicate resource file name: %s", f.getName());
        });
        this.files = ImmutableSet.copyOf(files);
        this.collection = null;
    }

    public Resources(ResourceCollection collection) {
        checkNotNull(collection, "resource collection must not be null");
        this.files = null;
        this.collection = collection;
    }

    public File lookup(String name, Evaluation.Context context) {
        if (collection != null) {
            return collection.lookup(name, context);
        }
        Optional<File> file = files.stream().filter(f -> f.getName().equals(name)).findFirst();
        if (file.isPresent()) {
            return file.get();
        } else {
            return null;
        }
    }

}
