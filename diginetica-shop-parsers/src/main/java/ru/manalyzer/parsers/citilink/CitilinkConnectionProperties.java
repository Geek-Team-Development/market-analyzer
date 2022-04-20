package ru.manalyzer.parsers.citilink;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.manalyzer.diginetica.property.DigineticaConnectionProperties;

/**
 * Parameters for connecting to the Citilink store.
 */
@Component
@PropertySource("classpath:citilink-connection.properties")
@ConfigurationProperties(value="shop.citilink")
public class CitilinkConnectionProperties extends DigineticaConnectionProperties {
}
