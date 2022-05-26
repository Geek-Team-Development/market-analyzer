package ru.manalyzer.dbmigrations;

import io.mongock.api.annotations.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import ru.manalyzer.persist.Notification;

@ChangeUnit(id = "init-collection-notifications", order = "8", author = "root")
public class NotificationCollectionInitializerChangeLog {

    private static final String INDEX_FIELD = "userId";

    private static final String INDEX_NAME = "ui_notification_userId";

    @BeforeExecution
    public void before(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection(Notification.class);
    }

    @RollbackBeforeExecution
    public void beforeRollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists(Notification.class)) {
            mongoTemplate.remove(Notification.class);
        }
    }

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(Notification.class).ensureIndex(
                new Index().on(INDEX_FIELD, Sort.Direction.ASC).named(INDEX_NAME).unique()
        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(Notification.class).dropIndex(INDEX_NAME);
    }
}
