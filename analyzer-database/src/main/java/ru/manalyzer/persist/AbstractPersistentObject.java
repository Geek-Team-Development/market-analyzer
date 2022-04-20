package ru.manalyzer.persist;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public abstract class AbstractPersistentObject {

    @Id
    private String id;

}
