package ru.manalyzer.parsers.oldi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.manalyzer.diginetica.property.DigineticaConnectionProperties;

/**
 * Parameters for connecting to the Oldi store.
 */
@Component
@PropertySource("classpath:oldi-connection.properties")
@ConfigurationProperties(value="shop.oldi")
public class OldiConnectionProperties extends DigineticaConnectionProperties {
}
