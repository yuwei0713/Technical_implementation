package com.example.intumit_project.config

import org.hibernate.SessionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.hibernate5.HibernateTransactionManager
import org.springframework.orm.hibernate5.LocalSessionFactoryBean
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration // 標記此類為 Spring 配置類，用於定義 Bean
class HibernateConfig {

    @Bean // 定義一個 DataSource Bean，提供資料庫連線資訊
    static DataSource dataSource() {
        // 創建並配置一個 DriverManagerDataSource 實例，用於連接到 MySQL 資料庫
        def dataSource = new DriverManagerDataSource()
        dataSource.driverClassName = "com.mysql.cj.jdbc.Driver" // 指定 MySQL JDBC 驅動類
        dataSource.url = "jdbc:mysql://localhost:3306/implement" // 資料庫 URL，連接到本機的 implement 資料庫
        dataSource.username = "yuwei" // 資料庫使用者名稱
        dataSource.password = "tp6m4jo3" // 資料庫密碼
        dataSource // 返回配置好的 DataSource
    }

    @Bean // 定義一個 LocalSessionFactoryBean，用於配置 Hibernate 的 SessionFactory
    static LocalSessionFactoryBean sessionFactory() {
        // 創建並配置 Hibernate 的 SessionFactory
        def sessionFactory = new LocalSessionFactoryBean()
        sessionFactory.dataSource = dataSource() // 設定資料來源，使用上面定義的 DataSource
        sessionFactory.packagesToScan = ["com.example.intumit_project.model"] // 指定掃描的套件，尋找 Hibernate 實體類
        sessionFactory.hibernateProperties = hibernateProperties() // 設定 Hibernate 的屬性
        sessionFactory // 返回配置好的 SessionFactory
    }

    // 定義 Hibernate 的屬性配置（私有靜態方法）
    private static Properties hibernateProperties() {
        // 創建一個 Properties 物件，用於儲存 Hibernate 配置
        def properties = new Properties()
        properties["hibernate.dialect"] = "org.hibernate.dialect.MySQLDialect" // 指定 MySQL 方言，讓 Hibernate 適配 MySQL
        properties["hibernate.show_sql"] = "true" // 顯示執行時的 SQL 語句，方便調試
        properties["hibernate.format_sql"] = "true" // 格式化輸出的 SQL，提升可讀性
        properties["hibernate.hbm2ddl.auto"] = "update" // 自動更新資料庫結構（若表不存在則創建，已存在則更新）
        properties // 返回配置好的屬性
    }

    @Bean // 定義一個 HibernateTransactionManager Bean，用於管理 Hibernate 事務
    static HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        // 創建並配置 Hibernate 事務管理器
        def txManager = new HibernateTransactionManager()
        txManager.sessionFactory = sessionFactory // 設定使用的 SessionFactory
        txManager // 返回配置好的事務管理器
    }
}