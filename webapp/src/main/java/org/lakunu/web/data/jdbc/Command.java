package org.lakunu.web.data.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Command<T> {

    private final DataSource dataSource;

    protected Command(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public final T run() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return doRun(connection);
        }
    }

    protected abstract T doRun(Connection connection) throws SQLException;
}
