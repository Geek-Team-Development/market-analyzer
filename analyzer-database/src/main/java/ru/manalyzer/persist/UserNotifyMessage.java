package ru.manalyzer.persist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "userNotifyMessages")
@CompoundIndex(def = "{'userId':1, 'notifyMessageId':id}", name = "uci_userId_notifyMessageId", unique = true)
public class UserNotifyMessage extends AbstractPersistentObject {
    @Field
    @Indexed(name = "un_userId", direction = IndexDirection.ASCENDING)
    private String userId;

    @Field
    @Indexed(name = "un_notifyMessageId", direction = IndexDirection.ASCENDING)
    private String notifyMessageId;

    @Field
    private boolean read;
}
