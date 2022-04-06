package ru.manalyzer.parser.mvideo.service;

/**
 * Интерфейс, предоставляющий минимальный набор заголовка "Cookie"
 * для осуществления запросов к серверу MVideo
 */
public interface CookieService {
    String getRequiredCookies();
}
