package ru.manalyzer.persist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "products")
@CompoundIndex(def = "{'productShopId':1, 'shopName':id}", name = "uci_productShopId_shopName")
public class Product extends AbstractPersistentObject {

    @Field
    private String productShopId;

    @Field
    private String shopName;

    @Field
    private String name;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal cost;

    @Field
    private String imageLink;

    @Field
    private String productLink;

}
