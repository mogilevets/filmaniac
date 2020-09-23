package com.mogilevets.filmaniac.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource({"classpath:application.properties"})
public class FilManiacConfig {

    @Autowired
    Environment env;

    @Value("${jdbc.driverClassName}")
    private String jdbcDriverName;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${jdbc.database}")
    private String jdbcDatabase;

    @Value("${jdbc.username}")
    private String jdbcUserName;

    @Value("${jdbc.password}")
    private String jdbcPassword;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
