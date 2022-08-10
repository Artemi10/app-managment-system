package com.devanmejia.appmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.devanmejia.appmanager.configuration")
public class AppManagerApplication {

    static {
        System.setProperty("user.timezone", "UTC");
    }

    public static void main(String[] args) {
        SpringApplication.run(AppManagerApplication.class, args);
    }

}
