package com.sap.grc.ctrl;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.sap.cloud.servicesdk.spring","com.sap.grc.ctrl"})
public class Application {
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
