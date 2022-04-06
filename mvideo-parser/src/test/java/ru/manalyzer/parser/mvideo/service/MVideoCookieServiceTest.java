package ru.manalyzer.parser.mvideo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.manalyzer.parser.mvideo.utils.TestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MVideoCookieServiceTest {

    private Properties properties;
    private WebClient webClient;
    private ClientAndServer mockServer;

    private static final String propertyFilePath = "/mvideo-cookie-service-test/test.properties";
    private String hostname;
    private Integer port;

    @BeforeAll
    public void beforeAll() throws IOException {
        properties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream(propertyFilePath)) {
            properties.load(inputStream);
        }
        hostname = properties.getProperty("mvideo.main.url");
        port = Integer.parseInt(properties.getProperty("mvideo.server.port"));

        mockServer = ClientAndServer.startClientAndServer(port);
        HttpClient httpClient = HttpClient.create()
                .httpResponseDecoder(spec -> spec.maxHeaderSize(Integer.MAX_VALUE));
        webClient = WebClient.builder()
                .baseUrl(hostname + ":" + port)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @AfterAll
    public void afterAll() {
        mockServer.stop();
    }

    @Test
    public void getRequiredCookiesTest() {
        String cookieResponseFile = properties.getProperty("mvideo.response.file");
        ResponseEntity<String> responseEntity =
                TestUtils.readFromFile(cookieResponseFile, new TypeReference<ResponseEntity<String>>() { });
        List<Header> headers = responseEntity
                .getHeaders()
                .entrySet()
                .stream()
                .map(entry -> new Header(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        mockServer
                .when(request()
                        .withMethod("GET").withPath("/"))
                .respond(response()
                        .withBody(responseEntity.getBody())
                        .withHeaders(headers));

        String expectedCookiesFileName = properties.getProperty("mvideo.expected-cookies.file");
        String expectedCookies = TestUtils.readFromFile(expectedCookiesFileName, String.class);
        MVideoCookieService cookieService = new MVideoCookieService(webClient);
        TestUtils.setPrivateFields(cookieService, properties);
        String resultCookies = cookieService.getRequiredCookies();
        assertEquals(expectedCookies, resultCookies);
    }
}
