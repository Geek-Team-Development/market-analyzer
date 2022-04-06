package ru.manalyzer.parser.mvideo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestUtils {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.addMixIn(ResponseEntity.class, ResponseEntityMixIn.class);
        mapper.addMixIn(HttpStatus.class, HttpStatusMixIn.class);
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setDefaultPrettyPrinter(printer);
    }

    public static Properties loadProperties(String filePath) {
        Properties properties = new Properties();
        String pathToProperties = Path.of(filePath).toString();
        try (InputStream inputStream = TestUtils.class.getResourceAsStream(pathToProperties)) {
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(String.format("Не могу загрузить файл свойств %s", filePath), e);
        }
    }

    public static <T> String writeAsString(T obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readFromFile(String fileName, Class<T> clazz) {
        String content = readFromFile(fileName);
        try {
            return mapper.readValue(content, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readFromFile(String fileName, TypeReference<? extends T> typeReference) {
        String content = readFromFile(fileName);
        try {
            return mapper.readValue(content, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readFromFile(String fileName) {

        URL url = TestUtils.class.getResource(fileName);
        assertNotNull(url);
        try {
            return Files.readString(Path.of(url.toURI()), StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void setPrivateFields(T obj, Properties properties) {
        Class<?> objClass = obj.getClass();
        Field[] declaredFields = objClass.getDeclaredFields();
        Stream.of(declaredFields)
                .forEach(field -> {
                    Value valueAnno = field.getAnnotation(Value.class);
                    if (valueAnno != null) {
                        String value = valueAnno.value()
                                .replace("${", "")
                                .replace("}", "");
                        Object settingValue;
                        if (field.getType().equals(Set.class)) {
                            settingValue = Arrays.stream(properties.getProperty(value).split(","))
                                    .collect(Collectors.toSet());
                        } else {
                            settingValue = properties.getProperty(value);
                        }
                        field.setAccessible(true);
                        ReflectionUtils.setField(field, obj, settingValue);
                    }
                });
    }
}
