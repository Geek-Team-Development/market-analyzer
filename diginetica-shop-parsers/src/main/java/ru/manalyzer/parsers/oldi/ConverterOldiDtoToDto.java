package ru.manalyzer.parsers.oldi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.manalyzer.diginetica.dto.ConverterDigineticaDtoToDto;
import ru.manalyzer.diginetica.property.DigineticaConnectionProperties;

/**
 * Performs conversions between dto objects of the online store and ProductDto
 */
@Component
public class ConverterOldiDtoToDto extends ConverterDigineticaDtoToDto {

    @Autowired
    public ConverterOldiDtoToDto(DigineticaConnectionProperties oldiConnectionProperties) {
        super(oldiConnectionProperties);
    }
}
