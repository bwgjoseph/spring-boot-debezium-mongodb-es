package com.bwgjoseph.springbootdebeziummongodbes.debezium;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Service;

import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DebeziumService {

    private final Executor executor;
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;

    public DebeziumService(Executor executor, Configuration mongodbConnector) {
        this.executor = Executors.newSingleThreadExecutor();
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
            .using(mongodbConnector.asProperties())
            .notifying(this::handleChangeEvent)
            .build();
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
        // log.info("sourceRecord {}", sourceRecord);
        // log.info("sourceRecord valueSchema {}", sourceRecord.valueSchema());
        // Struct sourceRecordChangeValue= (Struct) sourceRecord.value();
        // log.info("sourceRecordChangeValue {}", sourceRecordChangeValue);

        // if (sourceRecordChangeValue != null) {
        //     Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));

        //     log.info("operation {}", operation);

        //     if(operation != Operation.READ) {
        //         String record = operation == Operation.DELETE ? BEFORE : AFTER;
        //         Struct struct = (Struct) sourceRecordChangeValue.get(record);

        //         log.info("struct {}", struct);
        //     }
        // }

        log.info("Key = '" + sourceRecord.key() + "' value = '" + sourceRecord.value() + "'");
    }

    @PostConstruct
    private void start() {
        this.executor.execute(debeziumEngine);
    }

    @PreDestroy
    private void stop() throws IOException {
        if (this.debeziumEngine != null) {
            this.debeziumEngine.close();
        }
    }
}
