package ru.manalyzer.dbmigrations;

import io.mongock.api.annotations.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import ru.manalyzer.persist.Product;

@ChangeUnit(id = "init-collection-products", order = "4", author = "root")
public class ProductCollectionInitializerChangeLog {

    private static final String SHOP_ID_INDEX_FIELD = "productShopId";

    private static final String SHOP_NAME_INDEX_FIELD = "shopName";

    private static final String INDEX_NAME = "uci_productShopId_shopName";

    @BeforeExecution
    public void before(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection(Product.class);
    }

    @RollbackBeforeExecution
    public void beforeRollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists(Product.class)) {
            mongoTemplate.remove(Product.class);
        }
    }

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        Document indexOptions = new Document();
        indexOptions.put(SHOP_ID_INDEX_FIELD, 1);
        indexOptions.put(SHOP_NAME_INDEX_FIELD, 1);
        CompoundIndexDefinition indexDefinition = new CompoundIndexDefinition(indexOptions);

        mongoTemplate.indexOps(Product.class).ensureIndex(indexDefinition.named(INDEX_NAME).unique());
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(Product.class).dropIndex(INDEX_NAME);
    }
}
