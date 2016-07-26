package org.lakunu.web.dao.jdbc;

import org.lakunu.web.dao.CourseDAO;
import org.lakunu.web.service.DAOFactory;
import org.lakunu.web.utils.ConfigProperties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class JdbcDAOFactory extends DAOFactory {

    private static final String DAO_COLLECTION_DS = "daoCollection.ds";

    private final DataSource dataSource;

    public JdbcDAOFactory(ConfigProperties properties) {
        InitialContext context = null;
        try {
            context = new InitialContext();
            String dsName = properties.getRequired(DAO_COLLECTION_DS);
            logger.info("Loading JDBC datasource: {}", dsName);
            dataSource = (DataSource) context.lookup(dsName);
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
    protected CourseDAO getCourseDAO() {
        return new JdbcCourseDAO(dataSource);
    }
}
