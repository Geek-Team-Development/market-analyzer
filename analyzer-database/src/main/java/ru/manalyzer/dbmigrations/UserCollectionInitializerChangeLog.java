package ru.manalyzer.dbmigrations;

import io.mongock.api.annotations.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import ru.manalyzer.persist.User;

@ChangeUnit(id = "init-collection-users", order = "1", author = "root")
public class UserCollectionInitializerChangeLog {

    private static final String INDEX_EMAIL_FIELD = "email";

    private static final String INDEX_EMAIL_NAME = "ui_email";

    private static final String INDEX_CHAT_FIELD = "telegramChatId";

    private static final String INDEX_CHAT_NAME = "ui_chat";

    @BeforeExecution
    public void before(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection(User.class);
    }

    @RollbackBeforeExecution
    public void beforeRollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists(User.class)) {
            mongoTemplate.remove(User.class);
        }
    }

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(User.class).ensureIndex(
                new Index().on(INDEX_EMAIL_FIELD, Sort.Direction.ASC).named(INDEX_EMAIL_NAME).unique()
        );
//        mongoTemplate.indexOps(User.class).ensureIndex(
//                new Index().on(INDEX_CHAT_FIELD, Sort.Direction.ASC).named(INDEX_CHAT_NAME).unique()
//        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(User.class).dropIndex(INDEX_EMAIL_NAME);
    }

}
