package com.bwgjoseph.springbootdebeziummongodbes.debezium;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import io.debezium.config.CommonConnectorConfig;
import io.debezium.config.CommonConnectorConfig.EventProcessingFailureHandlingMode;
import io.debezium.config.Configuration;
import io.debezium.connector.mongodb.MongoDbConnectorConfig;
import io.debezium.embedded.EmbeddedEngine;

@org.springframework.context.annotation.Configuration
public class DebeziumConnectorConfig {
    // based off https://debezium.io/documentation/reference/2.2/connectors/mongodb.html#mongodb-connector-properties

    @Value("${dbz.offset.file}")
    public String storageFile;

    @Bean
    public Configuration mongodbConnector() {
        return Configuration.create()
                // engine properties
                .with(EmbeddedEngine.ENGINE_NAME, "sbd-mongodb-cdc")
                .with(EmbeddedEngine.CONNECTOR_CLASS, "io.debezium.connector.mongodb.MongoDbConnector")
                .with(EmbeddedEngine.OFFSET_STORAGE, "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with(EmbeddedEngine.OFFSET_STORAGE_FILE_FILENAME, storageFile)
                .with(EmbeddedEngine.OFFSET_FLUSH_INTERVAL_MS, "60000")
                // connector specific properties
                .with(CommonConnectorConfig.TOPIC_PREFIX, "dbz")
                .with(CommonConnectorConfig.SNAPSHOT_DELAY_MS, "100")
                // .with(CommonConnectorConfig.SNAPSHOT_FETCH_SIZE, "0") // has default
                .with(CommonConnectorConfig.SCHEMA_NAME_ADJUSTMENT_MODE, "none") // has default
                .with(CommonConnectorConfig.EVENT_PROCESSING_FAILURE_HANDLING_MODE, EventProcessingFailureHandlingMode.FAIL)
                // mongo connector specific properties
                .with(MongoDbConnectorConfig.CONNECTION_STRING, "mongodb://root:password@localhost:27017/source?directConnection=true&authSource=admin")
                // .with(MongoDbConnectorConfig.USER, "root")
                // .with(MongoDbConnectorConfig.PASSWORD, "password")
                // .with(MongoDbConnectorConfig.AUTH_SOURCE, "admin") // has default
                .with(MongoDbConnectorConfig.SSL_ENABLED, "false") // default false
                // .with(MongoDbConnectorConfig.SSL_ALLOW_INVALID_HOSTNAMES, "false") // has default
                .with(MongoDbConnectorConfig.DATABASE_INCLUDE_LIST, "source") // default empty
                // .with(MongoDbConnectorConfig.DATABASE_EXCLUDE_LIST, "") // has default
                // .with(MongoDbConnectorConfig.COLLECTION_INCLUDE_LIST, "") // has default
                // .with(MongoDbConnectorConfig.COLLECTION_EXCLUDE_LIST, "") // has default
                // .with(MongoDbConnectorConfig.SNAPSHOT_MODE, "initial") // has default
                .with(MongoDbConnectorConfig.CAPTURE_MODE, "change_streams_update_full_with_pre_image") // has default
                // .with(MongoDbConnectorConfig.FIELD_EXCLUDE_LIST, "") // has default
                // .with(MongoDbConnectorConfig.FIELD_RENAMES, "") // has default
                // .with(MongoDbConnectorConfig.SNAPSHOT_MAX_THREADS, "1") // has default
                // .with(MongoDbConnectorConfig.TOMBSTONES_ON_DELETE, "true") // has default
                .build();
    }
}
