package com.bwgjoseph.springbootdebeziummongodbes.debezium;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import io.debezium.config.Configuration;

@org.springframework.context.annotation.Configuration
public class DebeziumConnectorConfig {
    // based off https://debezium.io/documentation/reference/2.1/connectors/mongodb.html#mongodb-connector-properties

    @Value("${dbz.offset.file}")
    public String storageFile;

    @Bean
    public Configuration mongodbConnector() throws IOException {
        return Configuration.create()
                // engine properties
                .with("name", "sbd-mongodb-cdc")
                .with("connector.class", "io.debezium.connector.mongodb.MongoDbConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", storageFile)
                .with("offset.flush.interval.ms", "60000")
                // connector specific properties
                .with("mongodb.connection.string", "mongodb://root:password@localhost:27017/source?directConnection=true&authSource=admin")
                .with("topic.prefix", "dbz")
                // .with("mongodb.hosts", "sb-debezium.1ewsyzd.mongodb.net")
                // .with("mongodb.user", "root")
                // .with("mongodb.password", "password")
                // .with("mongodb.authsource", "admin") // has default
                .with("mongodb.ssl.enabled", "false") // default false
                // .with("mongodb.ssl.invalid.hostname.allowed", "false") // has default
                .with("database.include.list", "source") // default empty
                // .with("database.exclude.list", "") // has default
                // .with("collection.include.list", "") // has default
                // .with("collection.exclude.list", "") // has default
                // .with("snapshot.mode", "initial") // has default
                .with("capture.mode", "change_streams_update_full_with_pre_image") // has default
                // .with("snapshot.include.collection.list", "") // default collection.include.list
                // .with("field.exclude.list", "") // has default
                // .with("field.renames", "") // has default
                // .with("tasks.max", "1") // has default
                // .with("snapshot.max.threads", "1") // has default
                // .with("tombstones.on.delete", "true") // has default
                .with("snapshot.delay.ms", "100")
                // .with("snapshot.fetch.size", "0") // has default
                // .with("schema.name.adjustment.mode", "none") // has default
                // debug
                .with("errors.log.include.messages", "true")
                .build();
    }
}
