package com.example.serivcediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class SerivceDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SerivceDiscoveryApplication.class, args);
    }

}
