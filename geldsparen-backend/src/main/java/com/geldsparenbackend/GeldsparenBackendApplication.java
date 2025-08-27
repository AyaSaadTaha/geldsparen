package com.geldsparenbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.geldsparenbackend.model")
public class GeldsparenBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeldsparenBackendApplication.class, args);
    }

}
