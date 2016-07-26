package org.lakunu.web.data;

public interface DAOCollection {

    String DAO_COLLECTION = "DAO_COLLECTION";

    LabDAO getLabDAO();

    default void close() {

    }

}
