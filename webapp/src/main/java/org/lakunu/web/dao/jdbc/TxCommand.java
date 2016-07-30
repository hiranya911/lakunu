package org.lakunu.web.dao.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class TxCommand<T> extends Command<T> {

    protected final int txIsolationLevel;

    protected TxCommand(DataSource dataSource) {
       this(dataSource, -1);
    }

    protected TxCommand(DataSource dataSource, int txIsolationLevel) {
        super(dataSource);
        this.txIsolationLevel = txIsolationLevel;
    }

    @Override
    protected final T doRun(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        if (txIsolationLevel != -1) {
            connection.setTransactionIsolation(txIsolationLevel);
        }
        try {
            T result = doTransaction(connection);
            connection.commit();
            return result;
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    protected abstract T doTransaction(Connection connection) throws SQLException;
}
