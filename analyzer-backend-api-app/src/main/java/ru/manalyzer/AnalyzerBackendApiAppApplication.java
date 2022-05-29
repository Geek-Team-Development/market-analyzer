package ru.manalyzer;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableMongock
@EnableScheduling
public class AnalyzerBackendApiAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyzerBackendApiAppApplication.class);
    }

}
