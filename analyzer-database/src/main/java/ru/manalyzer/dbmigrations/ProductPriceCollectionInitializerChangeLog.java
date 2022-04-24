package ru.manalyzer.dbmigrations;

import io.mongock.api.annotations.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import ru.manalyzer.persist.ProductPrice;

@ChangeUnit(id = "init-collection-productPrices", order = "6", author = "root")
public class ProductPriceCollectionInitializerChangeLog {

    private static final String INDEX_FIELD = "productId";

    private static final String INDEX_NAME = "i_productId";

    @BeforeExecution
    public void before(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection(ProductPrice.class);
    }

    @RollbackBeforeExecution
    public void beforeRollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists(ProductPrice.class)) {
            mongoTemplate.remove(ProductPrice.class);
        }
    }

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(ProductPrice.class).ensureIndex(
                new Index().on(INDEX_FIELD, Sort.Direction.ASC).named(INDEX_NAME)
        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(ProductPrice.class).dropIndex(INDEX_NAME);
    }
}
