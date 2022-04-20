package ru.manalyzer.parsers.citilink;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.manalyzer.diginetica.DigineticaParser;
import ru.manalyzer.diginetica.dto.ConverterDigineticaDtoToDto;
import ru.manalyzer.diginetica.property.DigineticaConnectionProperties;

/**
 * Responsible for loading and converting product information from Citilink store
 */
@Service
@Log4j2
public class CitilinkParser extends DigineticaParser {

    @Autowired
    public CitilinkParser(DigineticaConnectionProperties citilinkConnectionProperties, ConverterDigineticaDtoToDto converterCitilinkDtoToDto) {
        super(citilinkConnectionProperties, converterCitilinkDtoToDto);
    }
}
