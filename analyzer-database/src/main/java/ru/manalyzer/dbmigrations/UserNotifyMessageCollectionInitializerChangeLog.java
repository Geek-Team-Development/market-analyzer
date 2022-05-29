package ru.manalyzer.dbmigrations;

import io.mongock.api.annotations.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import ru.manalyzer.persist.UserNotifyMessage;

@ChangeUnit(id = "init-collection-user-notify-messages", order = "9", author = "root")
public class UserNotifyMessageCollectionInitializerChangeLog {
    private static final String INDEX_FIELD = "userId";

    private static final String INDEX_NAME = "un_userId";

    @BeforeExecution
    public void before(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection(UserNotifyMessage.class);
    }

    @RollbackBeforeExecution
    public void beforeRollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists(UserNotifyMessage.class)) {
            mongoTemplate.remove(UserNotifyMessage.class);
        }
    }

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(UserNotifyMessage.class).ensureIndex(
                new Index().on(INDEX_FIELD, Sort.Direction.ASC).named(INDEX_NAME)
        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(UserNotifyMessage.class).dropIndex(INDEX_NAME);
    }
}
