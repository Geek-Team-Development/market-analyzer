package ru.manalyzer.parser.mvideo.service;

import org.springframework.http.HttpHeaders;

/**
 * Интерфейс, предоставлящий минимальный набор заголовков для
 * осуществления запросов:
 * 1. Получения искомых id продуктов
 * 2. Получения искомых цен продуктов
 * 3. Получения детализированной информации о найденных продуктах
 */
public interface HeadersService {
    HttpHeaders getIdsHeaders();
    HttpHeaders getPriceHeaders();
    HttpHeaders getDetailsHeaders(String searchName);
}
