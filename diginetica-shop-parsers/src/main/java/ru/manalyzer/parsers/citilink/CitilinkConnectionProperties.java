package ru.manalyzer.parsers.citilink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.manalyzer.diginetica.property.DigineticaConnectionProperties;

import java.util.Map;

/**
 * Parameters for connecting to the Citilink store.
 */
@Component
@PropertySource("classpath:citilink-connection.properties")
public class CitilinkConnectionProperties extends DigineticaConnectionProperties {

    @Autowired
    private CitilinkConnectionProperties(Environment environment) {
        super(environment, "citilink-connection.properties");
    }

    public CitilinkConnectionProperties(String shopName, String shopUri, String searchUri, String searchPath, String searchParamName, Map<String, String> queryParams) {
        super(shopName, shopUri, searchUri, searchPath, searchParamName, queryParams);
    }
}
