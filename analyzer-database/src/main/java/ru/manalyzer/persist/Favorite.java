package ru.manalyzer.persist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "favorites")
public class Favorite {

    @Id
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    @Indexed(name = "ui_userId")
    private String userId;


    @Field(targetType = FieldType.OBJECT_ID)
    @DocumentReference
    private List<Product> products = new ArrayList<>();
}
