package ru.manalyzer.config;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class MongockConfiguration {

    @Bean
    public MongockInitializingBeanRunner mongockRunner(ConnectionDriver driver, ApplicationContext applicationContext) {
        return MongockSpringboot.builder()
                .setDriver(driver)
                .setSpringContext(applicationContext)
                .addMigrationScanPackage("ru.manalyzer.dbmigrations")
                .withMetadata(
                        new HashMap() { {
                            put("change-motivation", "Missing field in collection");
                            put("decided-by", "Tom Waugh");
                        } })
                .setTrackIgnored(false)
                .setTransactionEnabled(false)
                .buildInitializingBeanRunner();
    }
}
