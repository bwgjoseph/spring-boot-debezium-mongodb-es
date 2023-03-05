package com.bwgjoseph.springbootdebeziummongodbes;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;

import io.debezium.config.Configuration;

@org.springframework.context.annotation.Configuration
public class DebeziumConnectorConfig {
    // based off https://debezium.io/documentation/reference/2.1/connectors/mongodb.html#mongodb-connector-properties
    @Bean
    public Configuration mongodbConnector() throws IOException {
        File offsetStorageTempFile = File.createTempFile("offsets_", ".dat");

        return Configuration.create()
                // engine properties
                .with("name", "sbd-mongodb")
                .with("connector.class", "io.debezium.connector.mongodb.MongoDbConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", offsetStorageTempFile.getAbsolutePath())
                .with("offset.flush.interval.ms", "60000")
                // connector specific properties
                // .with("mongodb.hosts", "sb-debezium.1ewsyzd.mongodb.net")
                .with("mongodb.connection.string", "mongodb+srv://sb-debezium.1ewsyzd.mongodb.net")
                .with("topic.prefix", "sbd-mongodb-connector")
                .with("mongodb.user", "bwgjoseph")
                .with("mongodb.password", "d8v2dYsu85i0QSRU")
                // .with("mongodb.authsource", "admin") // has default
                .with("mongodb.ssl.enabled", "true") // default false
                // .with("mongodb.ssl.invalid.hostname.allowed", "false") // has default
                .with("database.include.list", "source") // default empty
                // .with("database.exclude.list", "") // has default
                // .with("collection.include.list", "") // has default
                // .with("collection.exclude.list", "") // has default
                // .with("snapshot.mode", "initial") // has default
                // .with("capture.mode", "change_streams_update_full") // has default
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
