package ru.manalyzer.dbmigrations;

import io.mongock.api.annotations.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import ru.manalyzer.persist.Request;
import ru.manalyzer.persist.User;

@ChangeUnit(id = "init-collection-requests", order = "3", author = "root")
public class RequestCollectionInitializerChangeLog {

    private static final String USER_ID_INDEX_FIELD = "userId";

    private static final String USER_ID_INDEX_NAME = "i_userId";

    private static final String SEARCH_STRING_INDEX_FIELD = "searchString";

    private static final String SEARCH_STRING_INDEX_NAME = "i_searchString";

    @BeforeExecution
    public void before(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection(Request.class);
    }

    @RollbackBeforeExecution
    public void beforeRollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists(Request.class)) {
            mongoTemplate.remove(Request.class);
        }
    }

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(Request.class).ensureIndex(
                new Index().on(USER_ID_INDEX_FIELD, Sort.Direction.ASC).named(USER_ID_INDEX_NAME)
        );
        mongoTemplate.indexOps(Request.class).ensureIndex(
                new Index().on(SEARCH_STRING_INDEX_FIELD, Sort.Direction.ASC).named(SEARCH_STRING_INDEX_NAME)
        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(User.class).dropIndex(USER_ID_INDEX_NAME);
        mongoTemplate.indexOps(User.class).dropIndex(SEARCH_STRING_INDEX_NAME);
    }
}
