package ru.manalyzer.persist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "productPrices")
public class ProductPrice extends AbstractPersistentObject {

    @Field
    @Indexed(name = "productId", direction = IndexDirection.ASCENDING)
    private String productId;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;

    @Field(targetType = FieldType.DATE_TIME)
    private Date date;
}
