package ru.manalyzer.parser.mvideo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;

@Service
public class MVideoHeadersService implements HeadersService {
    @Value("${mvideo.host}")
    private String host;
    @Value("${mvideo.user-agent}")
    private String userAgent;
    @Value("${mvideo.product-details-referer}")
    private String productDetailsRefererUrl;

    private HttpHeaders commonHeaders;
    private HttpHeaders detailsHeaders;
    private final MVideoCookieService mVideoCookieService;


    @Autowired
    public MVideoHeadersService(MVideoCookieService mVideoCookieService) {
        this.mVideoCookieService = mVideoCookieService;
    }

    @PostConstruct
    public void init() {
        String requiredCookies = mVideoCookieService.getRequiredCookies();
        commonHeaders = new HttpHeaders();
        commonHeaders.set(HttpHeaders.HOST, host);
        commonHeaders.set(HttpHeaders.USER_AGENT, userAgent);
        commonHeaders.set(HttpHeaders.COOKIE, requiredCookies);
        detailsHeaders = new HttpHeaders();
        detailsHeaders.addAll(commonHeaders);
        detailsHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    public HttpHeaders getIdsHeaders() {
        return commonHeaders;
    }

    @Override
    public HttpHeaders getPriceHeaders() {
        return commonHeaders;
    }

    public HttpHeaders getDetailsHeaders(String searchName) {
        String referer = UriComponentsBuilder
                .fromHttpUrl(productDetailsRefererUrl)
                .queryParam("q", searchName)
                .build()
                .toString();
        detailsHeaders.set(HttpHeaders.REFERER, referer);
        return detailsHeaders;
    }
}
