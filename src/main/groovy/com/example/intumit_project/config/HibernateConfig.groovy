package com.example.intumit_project.config

import org.hibernate.SessionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.hibernate5.HibernateTransactionManager
import org.springframework.orm.hibernate5.LocalSessionFactoryBean
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class HibernateConfig {

    @Bean
    static DataSource dataSource() {
        def dataSource = new DriverManagerDataSource()
        dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
        dataSource.url = "jdbc:mysql://localhost:3306/implement"
        dataSource.username = "yuwei"
        dataSource.password = "tp6m4jo3"
        dataSource
    }

    @Bean
    static LocalSessionFactoryBean sessionFactory() {
        def sessionFactory = new LocalSessionFactoryBean()
        sessionFactory.dataSource = dataSource()
        sessionFactory.packagesToScan = ["com.example.intumit_project.model"]
        sessionFactory.hibernateProperties = hibernateProperties()
        sessionFactory
    }

    private static Properties hibernateProperties() {
        def properties = new Properties()
        properties["hibernate.dialect"] = "org.hibernate.dialect.MySQLDialect"
        properties["hibernate.show_sql"] = "true"
        properties["hibernate.format_sql"] = "true"
        properties["hibernate.hbm2ddl.auto"] = "update"
        properties
    }

    @Bean
    static HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        def txManager = new HibernateTransactionManager()
        txManager.sessionFactory = sessionFactory
        txManager
    }
}