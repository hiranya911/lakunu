package org.lakunu.web.data.jdbc;

import org.lakunu.web.data.DAOCollection;
import org.lakunu.web.data.LabDAO;
import org.lakunu.web.utils.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class JdbcDAOCollection implements DAOCollection {

    private static final String DAO_COLLECTION_DS = "daoCollection.ds";

    private static final Logger logger = LoggerFactory.getLogger(JdbcDAOCollection.class);

    private final JdbcLabDAO labDAO;

    public JdbcDAOCollection(ConfigProperties properties) {
        InitialContext context = null;
        try {
            context = new InitialContext();
            String dsName = properties.getRequired(DAO_COLLECTION_DS);
            logger.info("Loading JDBC datasource: {}", dsName);
            DataSource dataSource = (DataSource) context.lookup(dsName);
            this.labDAO = new JdbcLabDAO(dataSource);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            closeContext(context);
        }
    }

    private void closeContext(InitialContext context) {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException ignored) {
            }
        }
    }

    @Override
    public LabDAO getLabDAO() {
        return labDAO;
    }
}
