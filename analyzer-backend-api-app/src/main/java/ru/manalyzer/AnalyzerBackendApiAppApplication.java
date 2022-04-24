package ru.manalyzer;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableMongock
public class AnalyzerBackendApiAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyzerBackendApiAppApplication.class);
    }

}
