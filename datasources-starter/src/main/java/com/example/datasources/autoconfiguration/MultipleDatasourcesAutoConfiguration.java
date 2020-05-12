package com.example.datasources.autoconfiguration;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class MultipleDatasourcesAutoConfiguration {
    @Bean
    @Primary
    public DataSource dataSource(DataSource firstDataSource,
                                 DataSource secondDataSource) {
        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return ThreadLocalRandom.current().nextBoolean() ? "first" : "second";
            }
        };
        routingDataSource.setTargetDataSources(
                ImmutableMap.of(
                        "first", firstDataSource,
                        "second", secondDataSource
                ));
        return routingDataSource;
    }

    @Bean
    public DataSource firstDataSource() {
        BasicDataSource pool = new BasicDataSource();
        pool.setDriverClassName("org.hsqldb.jdbcDriver");
        pool.setUrl("jdbc:hsqldb:target/db1");
        pool.setUsername("sa");
        return pool;
    }

    @Bean
    public DataSource secondDataSource() {
        BasicDataSource pool = new BasicDataSource();
        pool.setDriverClassName("org.hsqldb.jdbcDriver");
        pool.setUrl("jdbc:hsqldb:target/db2");
        pool.setUsername("sa");
        return pool;
    }
}
