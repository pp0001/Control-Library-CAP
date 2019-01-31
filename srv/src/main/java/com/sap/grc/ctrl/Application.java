package com.sap.grc.ctrl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(basePackages = {"com.sap.cloud.servicesdk.spring","com.sap.grc.ctrl"})
@SpringBootApplication
@EntityScan("jpa.com.sap.grc.ctrl.test")
public class Application {
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
