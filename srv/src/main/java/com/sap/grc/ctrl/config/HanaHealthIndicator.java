package com.sap.grc.ctrl.config;

import javax.sql.DataSource;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.stereotype.Component;

@Component("dbHealthIndicator")
public class HanaHealthIndicator implements HealthIndicator {
    public static final String SELECT_1_FROM_DUMMY = "select 1 from dummy";
    DataSource dataSource;

    public HanaHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        DataSourceHealthIndicator dataSourceHealthIndicator = new DataSourceHealthIndicator(dataSource, SELECT_1_FROM_DUMMY);
        return dataSourceHealthIndicator.health();
    }

}
