package com.mogilevets.filmaniac.db;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:liquibase.properties"})
public class LiquibaseConfig {

    @Bean
    public SpringLiquibase liquibaseSet() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:db.changelog-master.yaml");
        return liquibase;
    }
}
