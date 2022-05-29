package ru.manalyzer.dbmigrations;

import io.mongock.api.annotations.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.manalyzer.persist.NotifyMessage;

@ChangeUnit(id = "init-collection-notify-messages", order = "7", author = "root")
public class NotifyMessageCollectionInitializerChangeLog {

    @Execution
    public void before(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection(NotifyMessage.class);
    }

    @RollbackExecution
    public void beforeRollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists(NotifyMessage.class)) {
            mongoTemplate.remove(NotifyMessage.class);
        }
    }

}
