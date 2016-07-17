package org.lakunu.web.data.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class TxCommand<T> extends Command<T> {

    protected TxCommand(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected final T doRun(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        try {
            T result = doTransaction(connection);
            connection.commit();
            return result;
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        }
    }

    protected abstract T doTransaction(Connection connection) throws SQLException;
}
