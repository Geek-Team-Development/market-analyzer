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
import ru.manalyzer.parser.mvideo.utils.TestUtils;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MVideoHeadersServiceTest {

    private MVideoHeadersService mVideoHeadersService;
    private MVideoCookieService mVideoCookieService;
    private MVideoProperties.Headers properties;
    private String requiredCookies;
    private static final String propertyFile = "/mvideo-headers-service/test.yaml";
    private static final String expectedCookiesFile = "/mvideo-cookie-service-test/expected-cookies.json";

    @BeforeAll
    public void beforeAll() throws IOException {
        Yaml yaml = new Yaml(new Constructor(MVideoProperties.Headers.class));
        try(InputStream inputStream = getClass().getResourceAsStream(propertyFile)) {
            properties = yaml.load(inputStream);
        }

        mVideoCookieService = mock(MVideoCookieService.class);
        requiredCookies = TestUtils.readFromFile(expectedCookiesFile, String.class);
        when(mVideoCookieService.getRequiredCookies()).thenReturn(requiredCookies);
    }

    @BeforeEach
    public void beforeEach() {
        mVideoHeadersService = new MVideoHeadersService(mVideoCookieService, properties);
        mVideoHeadersService.init();
    }

    @Test
    public void getIdsHeadersTest() {
        HttpHeaders expectedProductIdsHeaders = new HttpHeaders();
        expectedProductIdsHeaders.add(HttpHeaders.HOST, properties.getHost());
        expectedProductIdsHeaders.add(HttpHeaders.USER_AGENT, properties.getUserAgent());
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
        expectedHeaders.add(HttpHeaders.HOST, properties.getHost());
        expectedHeaders.add(HttpHeaders.USER_AGENT, properties.getUserAgent());
        expectedHeaders.add(HttpHeaders.COOKIE, requiredCookies);
        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
        String referer = properties.getProductDetailsReferer() + "?q=" + searchName;
        expectedHeaders.add(HttpHeaders.REFERER, referer);
        HttpHeaders resultHeaders = mVideoHeadersService.getDetailsHeaders(searchName);
        assertEquals(expectedHeaders, resultHeaders);
    }
}
