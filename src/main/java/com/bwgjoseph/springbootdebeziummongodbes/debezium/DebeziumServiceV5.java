package com.bwgjoseph.springbootdebeziummongodbes.debezium;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import com.bwgjoseph.springbootdebeziummongodbes.mongo.Person;

import io.debezium.config.Configuration;
import io.debezium.data.Envelope.Operation;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "mongo", name = "version", havingValue = "v5")
public class DebeziumServiceV5 {

    private final Executor executor;
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final ConversionService conversionService;
    private final ElasticsearchOperations elasticsearchOperations;

    public DebeziumServiceV5(Executor executor, Configuration mongodbConnector, ConversionService conversionService, ElasticsearchOperations elasticsearchOperations) {
        log.info("Starting DebeziumServiceV5");

        this.executor = Executors.newSingleThreadExecutor();
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
            .using(mongodbConnector.asProperties())
            .notifying(this::handleChangeEvent)
            .build();
        this.conversionService = conversionService;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    // see schema image on what are available
    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
        Struct sourceRecordValue = (Struct) sourceRecord.value();
        Struct sourceRecordKey = (Struct) sourceRecord.key();

        // on delete event, it seem that there is a second event (after delete) where the value is null
        // SourceRecord@224 "SourceRecord{sourcePartition={rs=atlas-b15fi5-shard-0, server_id=sbd-mongodb-connector}, sourceOffset={sec=1678550857, ord=1, transaction_id=null, resume_token=82640CA749000000012B022C0100296E5A1004CCDB188FE00C4DF2852FF333EE031E5646645F69640064640CA4A2F2146B0CFD4191390004}} ConnectRecord{topic='sbd-mongodb-connector.source.persons', kafkaPartition=null, key=Struct{id={"$oid": "640ca4a2f2146b0cfd419139"}}, keySchema=Schema{sbd-mongodb-connector.source.persons.Key:STRUCT}, value=null, valueSchema=null, timestamp=null, headers=ConnectHeaders(headers=)}"
        if (sourceRecordValue != null) {
            StructWrapper structWrapper = new StructWrapper(sourceRecordKey, sourceRecordValue, conversionService);

            log.info("envelop {}", structWrapper.getEnvelop());
            log.info("collection {}", structWrapper.getCollection());
            log.info("operation {}", structWrapper.getOperation());
            log.info("id {}", structWrapper.getId());

            // we ignore READ operation
            if (!structWrapper.getOperation().equals(Operation.READ)) {
                if (structWrapper.getOperation().equals(Operation.DELETE)) {
                    // delete from index
                    this.elasticsearchOperations.delete(structWrapper.getId(), IndexCoordinates.of("persons"));
                } else { // create or update
                    Person person = structWrapper.getRecord(Person.class);
                    log.info("record {}", person);

                    // we might want to transform, enrich, map to ES Person.class
                    com.bwgjoseph.springbootdebeziummongodbes.es.Person esPerson = this.conversionService.convert(person, com.bwgjoseph.springbootdebeziummongodbes.es.Person.class);
                    log.info("es person {}", esPerson);

                    // then index it into the store
                    IndexQuery indexQuery = new IndexQueryBuilder().withObject(esPerson).build();
                    this.elasticsearchOperations.index(indexQuery, IndexCoordinates.of("persons"));
                }
            }
        }

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
