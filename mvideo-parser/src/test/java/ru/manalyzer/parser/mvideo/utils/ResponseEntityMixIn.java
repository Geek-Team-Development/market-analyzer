package ru.manalyzer.parser.mvideo.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseEntityMixIn {
    @JsonCreator
    public ResponseEntityMixIn(@JsonProperty("body") Object body,
                               @JsonDeserialize(as = LinkedMultiValueMap.class)
                               @JsonProperty("headers") MultiValueMap<String, String> headers,
                               @JsonProperty("statusCodeValue") HttpStatus status) {

    }
}
