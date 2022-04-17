package ru.manalyzer.persist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "requests")
public class Request {

    @Id
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    @Indexed(name = "i_userId", direction = IndexDirection.ASCENDING)
    private String userId;

    @Field
    @Indexed(name = "i_searchId", direction = IndexDirection.ASCENDING)
    private String searchString;

    @Field(targetType = FieldType.DATE_TIME)
    public Date searchDate;

}
