package ru.manalyzer.parser.mvideo.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.manalyzer.parser.mvideo.config.MVideoProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MVideoCookieServiceTest {

    private WebClient webClient;
    private ClientAndServer mockServer;

    private static MVideoProperties.Cookies properties;
    private static final String propertyFile = "/mvideo-cookie-service-test/test.yaml";

    @BeforeAll
    public void beforeAll() throws IOException {
        Yaml yaml = new Yaml(new Constructor(MVideoProperties.Cookies.class));
        try(InputStream inputStream = getClass().getResourceAsStream(propertyFile)) {
            properties = yaml.load(inputStream);
        }
        String hostname = "http://localhost";
        Integer port = 8080;

        mockServer = ClientAndServer.startClientAndServer(port);
        webClient = WebClient.builder()
                .baseUrl(hostname + ":" + port)
                .build();
    }

    @AfterAll
    public void afterAll() {
        mockServer.stop();
    }

    @Test
    public void getRequiredCookiesTest() {
        String expectedCookies = getExpectedCookies();
        ResponseEntity<String> responseEntity = getExpectedCookieResponse(expectedCookies);
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

        MVideoCookieService cookieService = new MVideoCookieService(webClient, properties);
        String resultCookies = cookieService.getRequiredCookies();
        assertEquals(expectedCookies, resultCookies);
    }

    private ResponseEntity<String> getExpectedCookieResponse(String expectedCookies) {
        String body = "";
        MultiValueMap<String, String> headers = new HttpHeaders();
        Stream.of(expectedCookies.split("; "))
                .forEach(expectedCookie -> headers.add(HttpHeaders.SET_COOKIE, expectedCookie));
        IntStream.range(0, 5)
                .mapToObj(value -> UUID.randomUUID().toString())
                .forEach(headerName ->
                        headers.add(headerName, UUID.randomUUID().toString()));
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    private String getExpectedCookies() {
        Set<String> requiredCookies = properties.getRequiredNames();
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
