package ru.manalyzer.parser.mvideo.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.manalyzer.parser.mvideo.config.MVideoProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MVideoHeadersServiceTest {

    private MVideoHeadersService mVideoHeadersService;
    private MVideoCookieService mVideoCookieService;
    private MVideoProperties properties;
    private String requiredCookies;
    private static final String propertyFile = "/mvideo-headers-service/test.yaml";

    @BeforeAll
    public void beforeAll() throws IOException {
        Yaml yaml = new Yaml(new Constructor(MVideoProperties.class));
        try(InputStream inputStream = getClass().getResourceAsStream(propertyFile)) {
            properties = yaml.load(inputStream);
        }

        mVideoCookieService = mock(MVideoCookieService.class);
        requiredCookies = getExpectedCookies();
        when(mVideoCookieService.getRequiredCookies()).thenReturn(requiredCookies);
    }

    @BeforeEach
    public void beforeEach() {
        mVideoHeadersService = new MVideoHeadersService(mVideoCookieService, properties.getHeaders());
        mVideoHeadersService.init();
    }

    @Test
    public void getIdsHeadersTest() {
        HttpHeaders expectedProductIdsHeaders = new HttpHeaders();
        expectedProductIdsHeaders.add(HttpHeaders.HOST, properties.getHeaders().getHost());
        expectedProductIdsHeaders.add(HttpHeaders.USER_AGENT, properties.getHeaders().getUserAgent());
        expectedProductIdsHeaders.add(HttpHeaders.COOKIE, requiredCookies);
        HttpHeaders result = mVideoHeadersService.getIdsHeaders();
        assertEquals(expectedProductIdsHeaders, result);
    }

    @Test
    public void getPriceHeadersTest() {
        getIdsHeadersTest();
    }

    @Test
    public void getDetailsHeadersTest() {
        String searchName = "холодильники и ноутбуки";
        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add(HttpHeaders.HOST, properties.getHeaders().getHost());
        expectedHeaders.add(HttpHeaders.USER_AGENT, properties.getHeaders().getUserAgent());
        expectedHeaders.add(HttpHeaders.COOKIE, requiredCookies);
        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
        String referer = properties.getHeaders().getProductDetailsReferer() + "?q=" + searchName;
        expectedHeaders.add(HttpHeaders.REFERER, referer);
        HttpHeaders resultHeaders = mVideoHeadersService.getDetailsHeaders(searchName);
        assertEquals(expectedHeaders, resultHeaders);
    }

    private String getExpectedCookies() {
        Set<String> requiredCookies = properties.getCookies().getRequiredNames();
        return requiredCookies
                .stream()
                .collect(Collectors.toMap(Function.identity(),
                        requiredCookie -> UUID.randomUUID().toString()))
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}
