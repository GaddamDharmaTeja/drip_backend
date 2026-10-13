package com.dripgrow.api.config;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class GridFsConfig {

    /** Creates GridFS collections: uploads.files + uploads.chunks */
    @Bean
    public GridFsTemplate gridFsTemplate(MongoDatabaseFactory factory, MongoConverter converter) {
        return new GridFsTemplate(factory, converter, "uploads");
    }

    @Bean
    public GridFSBucket gridFsBucket(MongoDatabaseFactory factory) {
        MongoDatabase db = factory.getMongoDatabase();
        return GridFSBuckets.create(db, "uploads");
    }
}
