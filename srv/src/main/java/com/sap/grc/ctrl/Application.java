package com.sap.grc.ctrl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(basePackages = {"com.sap.cloud.servicesdk.spring","com.sap.grc.ctrl"})
@SpringBootApplication
@ServletComponentScan
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = { "com.sap.grc.ctrl" })
public class Application {
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
