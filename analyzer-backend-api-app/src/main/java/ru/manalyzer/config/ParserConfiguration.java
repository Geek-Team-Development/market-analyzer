package ru.manalyzer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.manalyzer.Parser;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class ParserConfiguration {

    @Autowired
    private List<Parser> parsers;

    @Bean
    public Map<String, Parser> activeParserMap() {
        return parsers.stream()
                .collect(Collectors.toMap(parser -> parser.getShopName(), Function.identity()));
    }
}
