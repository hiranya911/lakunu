package org.lakunu.web.data.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.lakunu.web.data.CourseDAO;
import org.lakunu.web.data.DAOCollection;
import org.lakunu.web.utils.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.sql.SQLException;

public final class JDBCDAOCollection implements DAOCollection {

    public static final String JDBC_PROPERTIES = "/WEB-INF/jdbc.properties";

    private static final Logger logger = LoggerFactory.getLogger(JDBCDAOCollection.class);

    private final BasicDataSource dataSource;
    private final JDBCCourseDAO courseDAO;

    public JDBCDAOCollection(ServletContext servletContext) {
        ConfigProperties properties;
        try {
            properties = new ConfigProperties(servletContext, JDBC_PROPERTIES);
            logger.info("Loaded JDBC configuration from: {}", JDBC_PROPERTIES);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JDBC configuration", e);
        }
        this.dataSource = new BasicDataSource();
        this.dataSource.setUrl(properties.getRequired("url"));
        this.dataSource.setDriverClassName(properties.getRequired("driver"));
        this.dataSource.setUsername(properties.getRequired("username"));
        this.dataSource.setPassword(properties.getOptional("password", null));
        logger.info("JDBC datasource initialized: {}", this.dataSource.getUrl());

        this.courseDAO = new JDBCCourseDAO(this.dataSource);
    }

    @Override
    public CourseDAO getCourseDAO() {
        return courseDAO;
    }

    @Override
    public void close() {
        try {
            this.dataSource.close();
        } catch (SQLException e) {
            logger.warn("Error while closing JDBC datasource", e);
        }
    }
}
