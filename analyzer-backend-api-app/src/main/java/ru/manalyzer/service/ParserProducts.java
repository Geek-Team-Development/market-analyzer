package ru.manalyzer.service;

import ru.manalyzer.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParserProducts {
    private final List<ProductDto> products = new ArrayList<>();
    private int currentPage;
    private int currentIndex;
    private String parserName;

    public void add(ProductDto productDto) {
        this.products.add(productDto);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getParserName() {
        return parserName;
    }

    public void setParserName(String parserName) {
        this.parserName = parserName;
    }

    public void clear() {
        products.clear();
        currentIndex = 0;
        currentPage = 0;
    }

    public int getSize() {
        return products.size() - currentIndex;
    }

    public List<ProductDto> getNextProducts() {
        return products.stream()
                .skip(currentIndex)
                .collect(Collectors.toList());
    }

    public void incrementIndex() {
        currentIndex++;
    }
}