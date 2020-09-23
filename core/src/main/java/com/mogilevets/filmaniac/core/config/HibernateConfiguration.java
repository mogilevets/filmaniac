package com.mogilevets.filmaniac.core.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.properties"})
public class HibernateConfiguration {

    private final Environment environment;

    @Autowired
    public HibernateConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public SessionFactory sessionFactory() {
        LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(this.dataSource());
        builder.scanPackages("com.mogilevets.*").addProperties(getHibernateProperties());
        return builder.buildSessionFactory();
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.format_sql", this.environment.getRequiredProperty("hibernate.format_sql"));
        properties.put("hibernate.show_sql", this.environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.dialect", this.environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.use_sql_comments", this.environment.getRequiredProperty("hibernate.use_sql_comments"));
        properties.put("hibernate.connection.autocommit", this.environment.getRequiredProperty("hibernate.connection.autocommit"));
        properties.put("org.hibernate.flushMode", "MANUAL");
        properties.put("hibernate.jdbc.lob.non_contextual_creation", true);
        return properties;
    }

    @Bean
    public HibernateTransactionManager txManager() {
        return new HibernateTransactionManager(sessionFactory());
    }

    @Bean
    public ComboPooledDataSource dataSource() {
        createDB();
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(this.environment.getRequiredProperty("jdbc.driverClassName"));
        } catch (PropertyVetoException e) {
            System.out.println("Cannot load datasource driver");
            e.printStackTrace();
        }
        String dbName = this.environment.getRequiredProperty("jdbc.database");
        dataSource.setJdbcUrl(this.environment.getRequiredProperty("jdbc.url") + dbName);
        dataSource.setUser(this.environment.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(this.environment.getRequiredProperty("jdbc.password"));
        dataSource.setMinPoolSize(5);
        dataSource.setMaxPoolSize(20);
        dataSource.setMaxIdleTime(300);
        return dataSource;
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:db.changelog-master.yaml");
        liquibase.setDataSource(dataSource());
        return liquibase;
    }

    private void createDB() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        Statement checkStatement = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(this.environment.getRequiredProperty("jdbc.url") + "postgres",
                    this.environment.getRequiredProperty("jdbc.username"), this.environment.getRequiredProperty("jdbc.password"));
            String dbName = this.environment.getRequiredProperty("jdbc.database");;
            checkStatement = connection.createStatement();
            resultSet = checkStatement.executeQuery(String.format("SELECT datname FROM pg_database WHERE datistemplate = FALSE AND datname = '%s'", dbName));
            if (!resultSet.next()) {
                statement = connection.createStatement();
                statement.executeUpdate(String.format("CREATE DATABASE %s", dbName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (checkStatement != null) checkStatement.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
