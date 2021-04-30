package com.weshare.batch.dataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class SecondDatasourceConfig {

    @Bean("secondDataSource")
    @ConfigurationProperties("spring.second.datasource")
    public DataSource secondDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean//("secondJdbcTemplate")
    public JdbcTemplate secondJdbcTemplate() {
        return new JdbcTemplate(secondDataSource());
    }

}


