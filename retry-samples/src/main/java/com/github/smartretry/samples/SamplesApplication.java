package com.github.smartretry.samples;

import com.github.smartretry.spring4.EnableRetrying;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRetrying
@SpringBootApplication
public class SamplesApplication {
    public static void main(String[] args) {
        SpringApplication.run(SamplesApplication.class, args);
    }
}
