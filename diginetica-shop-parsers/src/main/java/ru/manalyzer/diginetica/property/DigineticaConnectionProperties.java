package ru.manalyzer.diginetica.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
