package ru.manalyzer.controller.param;

import lombok.*;
import ru.manalyzer.dto.Sort;

import java.util.Optional;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductRequestParam {

    private String searchName;

    private Optional<String> pageNumber;

    private Optional<Sort> sort;

}
