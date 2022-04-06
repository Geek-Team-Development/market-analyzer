package ru.manalyzer.parser.mvideo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.HttpCookie;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MVideoCookieService implements CookieService {

    @Value("${mvideo.required-cookie-names}")
    private Set<String> requiredCookieNames;

    private final WebClient webClient;

    @Autowired
    public MVideoCookieService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String getRequiredCookies() {
        List<String> cookies = getResponseCookies();
        List<HttpCookie> parsedCookies = getParsedCookies(cookies);
        return parsedCookies.stream()
                .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                .collect(Collectors.joining("; "));
    }

    private List<String> getResponseCookies() {
        ResponseEntity<Void> entity = webClient
                .get()
                .retrieve()
                .toBodilessEntity()
                .block();
        return Optional
                .ofNullable(entity)
                .orElseThrow(() -> new RuntimeException(""))
                .getHeaders()
                .get(HttpHeaders.SET_COOKIE.toLowerCase());
    }

    private List<HttpCookie> getParsedCookies(List<String> cookies) {
        return cookies.stream()
                .filter(this::match)
                .limit(requiredCookieNames.size())
                .map(cookie -> HttpCookie.parse(cookie).get(0))
                .collect(Collectors.toList());
    }

    private boolean match(String cookie) {
        return requiredCookieNames.stream()
                .anyMatch(cookie::startsWith);
    }
}
