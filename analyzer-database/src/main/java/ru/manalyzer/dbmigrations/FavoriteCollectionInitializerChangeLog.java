package ru.manalyzer.dbmigrations;

import io.mongock.api.annotations.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import ru.manalyzer.persist.Favorite;

@ChangeUnit(id = "init-collection-favorites", order = "5", author = "root")
public class FavoriteCollectionInitializerChangeLog {

    private static final String INDEX_FIELD = "userId";

    private static final String INDEX_NAME = "ui_userId";

    @BeforeExecution
    public void before(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection(Favorite.class);
    }

    @RollbackBeforeExecution
    public void beforeRollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists(Favorite.class)) {
            mongoTemplate.remove(Favorite.class);
        }
    }

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(Favorite.class).ensureIndex(
                new Index().on(INDEX_FIELD, Sort.Direction.ASC).named(INDEX_NAME).unique()
        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(Favorite.class).dropIndex(INDEX_NAME);
    }
}
