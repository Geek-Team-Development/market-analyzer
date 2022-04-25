package ru.manalyzer.dbmigrations;

import io.mongock.api.annotations.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import ru.manalyzer.persist.User;

@ChangeUnit(id = "init-collection-users", order = "1", author = "root")
public class UserCollectionInitializerChangeLog {

    private static final String INDEX_FIELD = "email";

    private static final String INDEX_NAME = "ui_email";

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
                new Index().on(INDEX_FIELD, Sort.Direction.ASC).named(INDEX_NAME).unique()
        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(User.class).dropIndex(INDEX_NAME);
    }

}
