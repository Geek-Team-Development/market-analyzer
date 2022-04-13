package ru.manalyzer.parsers.oldi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.manalyzer.diginetica.property.DigineticaConnectionProperties;

import java.util.Map;

/**
 * Parameters for connecting to the Oldi store.
 */
@Component
@PropertySource("classpath:oldi-connection.properties")
public class OldiConnectionProperties extends DigineticaConnectionProperties {

    @Autowired
    private OldiConnectionProperties(Environment environment) {
        super(environment, "oldi-connection.properties");
    }

    public OldiConnectionProperties(String shopName, String shopUri, String searchUri, String searchPath, String searchParamName, Map<String, String> queryParams) {
        super(shopName, shopUri, searchUri, searchPath, searchParamName, queryParams);
    }
}
