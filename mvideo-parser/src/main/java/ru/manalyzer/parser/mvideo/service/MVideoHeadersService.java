package ru.manalyzer.parser.mvideo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.manalyzer.parser.mvideo.config.properties.HeadersProperties;

import javax.annotation.PostConstruct;

@Service
public class MVideoHeadersService implements HeadersService {

    private HttpHeaders commonHeaders;
    private HttpHeaders detailsHeaders;
    private final MVideoCookieService mVideoCookieService;
    private final HeadersProperties properties;

    @Autowired
    public MVideoHeadersService(MVideoCookieService mVideoCookieService,
                                HeadersProperties properties) {
        this.mVideoCookieService = mVideoCookieService;
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        String requiredCookies = mVideoCookieService.getRequiredCookies();
        commonHeaders = new HttpHeaders();
        commonHeaders.set(HttpHeaders.HOST, properties.getHost());
        commonHeaders.set(HttpHeaders.USER_AGENT, properties.getUserAgent());
        commonHeaders.set(HttpHeaders.COOKIE, requiredCookies);
        detailsHeaders = new HttpHeaders();
        detailsHeaders.addAll(commonHeaders);
        detailsHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public HttpHeaders getIdsHeaders() {
        return commonHeaders;
    }

    @Override
    public HttpHeaders getPriceHeaders() {
        return commonHeaders;
    }

    @Override
    public HttpHeaders getDetailsHeaders(String searchName) {
        String referer = UriComponentsBuilder
                .fromHttpUrl(properties.getProductDetailsReferer())
                .queryParam("q", searchName)
                .build()
                .toString();
        detailsHeaders.set(HttpHeaders.REFERER, referer);
        return detailsHeaders;
    }
}
