package com.apten.household;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HouseholdServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HouseholdServiceApplication.class, args);
    }
}
