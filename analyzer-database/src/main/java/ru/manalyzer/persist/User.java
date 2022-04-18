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

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User extends AbstractPersistentObject {

    @Field
    @Indexed(name = "ui_email", direction = IndexDirection.ASCENDING)
    private String email;

    @Field
    private String firstname;

    @Field
    private String lastname;

    @Field
    private String password;

    @Field
    private String city;

    @Field(targetType = FieldType.STRING)
    private List<Role> roles = new ArrayList<>();

}
