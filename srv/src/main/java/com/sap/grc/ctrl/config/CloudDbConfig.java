package com.sap.grc.ctrl.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@Profile({"cloud"})
public class CloudDbConfig {
    private static Logger logger = LoggerFactory.getLogger(CloudDbConfig.class);
    private static final String VCAP_SERVICES = "vcap.services.";
    @Value("${db_service_instance_name}")
    private String hanaInstanceName;

    @Bean
    public DataSource dataSource(Environment env) {
        logger.info("Building datasource");
        logger.info("Hana Instance name is: " + hanaInstanceName);
        String driver = env.getProperty(VCAP_SERVICES + hanaInstanceName + ".credentials.driver", "");
        String url = env.getProperty(VCAP_SERVICES + hanaInstanceName + ".credentials.url", "");
        String user = env.getProperty(VCAP_SERVICES + hanaInstanceName + ".credentials.user", "");
        String password = env.getProperty(VCAP_SERVICES + hanaInstanceName + ".credentials.password", "");
        DataSource dataSource = null;
        dataSource = DataSourceBuilder.create().driverClassName(driver).type(com.zaxxer.hikari.HikariDataSource.class)
                .url(url)
                .username(user)
                .password(password).build();
        logger.info("datasource:" + dataSource.toString());
        return dataSource;
    }

}