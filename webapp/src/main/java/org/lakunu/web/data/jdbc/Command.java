package org.lakunu.web.data.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A Command encapsulates the execution of one or more JDBC queries using an instance of
 * {@link java.sql.Connection}. A new connection is obtained from a DataSource for each
 * execution of the command. Ensures that the connection is closed at the end of the
 * query execution. Command instances are expected to be very lightweight. Therefore
 * subclasses should not perform any expensive input validation. The parent Command class
 * does not perform any validation by itself. The callers of the Command class must
 * perform any input validation, before constructing and invoking the Command instances.
 *
 * @param <T> Return type of the command.
 */
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
