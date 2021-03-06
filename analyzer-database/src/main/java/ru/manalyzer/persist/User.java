package ru.manalyzer.persist;

import lombok.*;
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
    private String firstName;

    @Field
    private String lastName;

    @Field
    private String password;

    @Field
    private String city;

    @Field
    private String telegramChatId;

    @Field(targetType = FieldType.STRING)
    private List<Role> roles = new ArrayList<>();

}
