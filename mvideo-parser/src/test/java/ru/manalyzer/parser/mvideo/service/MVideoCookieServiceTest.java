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
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import reactor.netty.http.client.HttpClient;
import ru.manalyzer.parser.mvideo.config.MVideoProperties;
import ru.manalyzer.parser.mvideo.utils.TestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MVideoCookieServiceTest {

    private WebClient webClient;
    private ClientAndServer mockServer;

    private static MVideoProperties.Cookies properties;
    private static final String propertyFile = "/mvideo-cookie-service-test/test.yaml";
    private static final String cookieResponseFile = "/mvideo-cookie-service-test/cookie-response.json";
    private static final String expectedCookiesFileName = "/mvideo-cookie-service-test/expected-cookies.json";

    @BeforeAll
    public void beforeAll() throws IOException {
        Yaml yaml = new Yaml(new Constructor(MVideoProperties.Cookies.class));
        try(InputStream inputStream = getClass().getResourceAsStream(propertyFile)) {
            properties = yaml.load(inputStream);
        }
        String hostname = "http://localhost";
        Integer port = 8080;

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

        String expectedCookies = TestUtils.readFromFile(expectedCookiesFileName, String.class);
        MVideoCookieService cookieService = new MVideoCookieService(webClient, properties);
        String resultCookies = cookieService.getRequiredCookies();
        assertEquals(expectedCookies, resultCookies);
    }
}
