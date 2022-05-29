package ru.manalyzer.persist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notifications")
public class Notification extends AbstractPersistentObject {

    @Field(targetType = FieldType.OBJECT_ID)
    @Indexed(name = "un_userId")
    private String userId;

    @Field(targetType = FieldType.OBJECT_ID)
    @DocumentReference
    private Set<NotifyMessage> messages = new HashSet<>();
}
