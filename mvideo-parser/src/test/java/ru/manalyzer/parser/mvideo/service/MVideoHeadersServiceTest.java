package ru.manalyzer.parser.mvideo.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.manalyzer.parser.mvideo.utils.TestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MVideoHeadersServiceTest {

    private MVideoHeadersService mVideoHeadersService;
    private MVideoCookieService mVideoCookieService;
    private Properties properties;
    private String requiredCookies;
    private static final String propertyFilePath = "/mvideo-headers-service/test.properties";

    @BeforeAll
    public void beforeAll() throws IOException {
        properties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream(propertyFilePath)) {
            properties.load(inputStream);
        }

        mVideoCookieService = mock(MVideoCookieService.class);
        requiredCookies = TestUtils.readFromFile(properties.getProperty("required-cookies"), String.class);
        when(mVideoCookieService.getRequiredCookies()).thenReturn(requiredCookies);
    }

    @BeforeEach
    public void beforeEach() {
        mVideoHeadersService = new MVideoHeadersService(mVideoCookieService);
        TestUtils.setPrivateFields(mVideoHeadersService, properties);
        mVideoHeadersService.init();
    }

    @Test
    public void getIdsHeadersTest() {
        HttpHeaders expectedProductIdsHeaders = new HttpHeaders();
        expectedProductIdsHeaders.add(HttpHeaders.HOST, properties.getProperty("mvideo.host"));
        expectedProductIdsHeaders.add(HttpHeaders.USER_AGENT, properties.getProperty("mvideo.user-agent"));
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
        expectedHeaders.add(HttpHeaders.HOST, properties.getProperty("mvideo.host"));
        expectedHeaders.add(HttpHeaders.USER_AGENT, properties.getProperty("mvideo.user-agent"));
        expectedHeaders.add(HttpHeaders.COOKIE, requiredCookies);
        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
        String referer = properties.getProperty("mvideo.product-details-referer") + "?q=" + searchName;
        expectedHeaders.add(HttpHeaders.REFERER, referer);
        HttpHeaders resultHeaders = mVideoHeadersService.getDetailsHeaders(searchName);
        assertEquals(expectedHeaders, resultHeaders);
    }
}
