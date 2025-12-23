package com.recyclestudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class RecyclestudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecyclestudyApplication.class, args);
    }

}
