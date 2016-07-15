package org.lakunu.web.data;

public final class Course {
    
    private final String name;
    private final String owner;

    public Course(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }
}
