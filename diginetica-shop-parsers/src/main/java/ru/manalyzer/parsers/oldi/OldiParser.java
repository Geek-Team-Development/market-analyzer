package ru.manalyzer.parsers.oldi;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.manalyzer.diginetica.dto.ConverterDigineticaDtoToDto;
import ru.manalyzer.diginetica.DigineticaParser;
import ru.manalyzer.diginetica.property.DigineticaConnectionProperties;

/**
 * Responsible for loading and converting product information from Oldi store
 */
@Service
@Log4j2
public class OldiParser extends DigineticaParser {

    @Autowired
    public OldiParser(DigineticaConnectionProperties oldiConnectionProperties, ConverterDigineticaDtoToDto converterOldiDtoToDto) {
        super(oldiConnectionProperties, converterOldiDtoToDto);
    }
}
