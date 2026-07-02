package com.gabriel.twoforms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.gabriel.twoforms.entity")
@EnableJpaRepositories(basePackages = "com.gabriel.twoforms.repository")
public class BankMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankMSApplication.class, args);
    }
}
