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

    private int pageNumber = 0;

    private Sort sort = Sort.price_asc;

}
