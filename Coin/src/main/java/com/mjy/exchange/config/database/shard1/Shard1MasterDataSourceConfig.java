package com.mjy.exchange.config.database.shard1;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.mjy.exchange.repository.shard1.master",
        entityManagerFactoryRef = "shard1MasterEntityManagerFactory",
        transactionManagerRef = "shard1MasterTransactionManager"
)
public class Shard1MasterDataSourceConfig {

    @Bean(name = "shard1MasterDataSource")
    @ConfigurationProperties(prefix = "spring.shard1.master.datasource.hikari")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "shard1MasterEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("shard1MasterDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");

        return builder
                .dataSource(dataSource)
                .packages("com.mjy.exchange.entity.shard1")
                .persistenceUnit("master")
                .properties(properties)
                .build();
    }

    @Bean(name = "shard1MasterTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("shard1MasterEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
