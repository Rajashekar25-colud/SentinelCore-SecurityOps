package com.sentinelcore.secureops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SecureOpsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecureOpsApplication.class, args);
    }
}
