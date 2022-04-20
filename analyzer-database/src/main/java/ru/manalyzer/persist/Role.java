package ru.manalyzer.persist;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public enum Role {

    ADMIN,
    USER,
    BUSINESS,
    GUEST;

    Role() {
    }
}
