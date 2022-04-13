package ru.manalyzer.diginetica.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Parameters for connecting to the Diginetica store.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DigineticaConnectionProperties {

    protected String shopName;

    protected String shopUri;

    protected String searchUri;

    protected String searchPath;

    protected String searchParamName;

    protected Map<String, String> queryParams = new HashMap<>();

    protected DigineticaConnectionProperties(Environment environment, String propertyName) {
        PropertySource<?> propertySource = ((AbstractEnvironment) environment)
                .getPropertySources()
                .get(String.format("class path resource [%s]", propertyName)); //TODO Get property file name from annotation
        Assert.notNull(propertySource, "no such property");
        this.shopName = (String) propertySource.getProperty("shop.shopName");
        this.shopUri = (String) propertySource.getProperty("shop.shopUri");
        this.searchUri = (String) propertySource.getProperty("shop.searchUri");
        this.searchPath = (String) propertySource.getProperty("shop.searchPath");
        this.searchParamName = (String) propertySource.getProperty("shop.searchParamName");
        this.queryParams.put("apiKey", (String) propertySource.getProperty("shop.queryParams.apiKey"));
        this.queryParams.put("fullData", (String) propertySource.getProperty("shop.queryParams.fullData"));
    }
}
