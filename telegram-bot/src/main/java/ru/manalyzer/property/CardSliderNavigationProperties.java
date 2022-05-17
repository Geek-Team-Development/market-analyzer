package ru.manalyzer.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PropertySource("classpath:telegram.properties")
@ConfigurationProperties(value = "card-slider.navigation")
public class CardSliderNavigationProperties {

    private String buttonNextFormat;

    private String buttonPreviousFormat;

    private String buttonPageFormat;
}
