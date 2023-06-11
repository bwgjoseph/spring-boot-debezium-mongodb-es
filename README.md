# Spring Boot Debezium MongoDB to Elasticsearch

This project is used to learn on how to setup (embedded) Debezium with Spring Boot that connects to MongoDB (source) and push data to Elasticsearch (sink).

## References:

- [debezium-spring-boot](https://hevodata.com/learn/debezium-spring-boot)
- [debezium-mongodb](https://hevodata.com/learn/debezium-mongodb/)
- [debezium-mongodb-connector](https://debezium.io/documentation/reference/2.1/connectors/mongodb.html)
- [debezium-intro](https://www.baeldung.com/debezium-intro)
- [which-versions-of-mongodb-do-service-clusters-use-](https://www.mongodb.com/docs/atlas/reference/faq/database/#which-versions-of-mongodb-do-service-clusters-use-)
- [debezium/debezium-examples/using-mongodb](https://github.com/debezium/debezium-examples/tree/main/tutorial#using-mongodb)
- [kafka-edu](https://github.com/mongodb-university/kafka-edu)
- [change-data-capture-mongodb-debezium](https://redpanda.com/blog/change-data-capture-mongodb-debezium)
- [keeping-elasticsearch-in-sync-with-mongodb-using-change-streams](https://www.hownowbrownkow.com/posts/keeping-elasticsearch-in-sync-with-mongodb-using-change-streams)

---

## Account

Altas:
    - bwgjoseph / d8v2dYsu85i0QSRU / mongodb+srv://bwgjoseph:<password>@sb-debezium.1ewsyzd.mongodb.net/source

## Known Issue

`debezium-connector-mongodb-2.1.2.Final` brings in `mongo-driver-sync` which brings in `mongo-driver-core:4.6.1`, it does not have `FullDocumentBeforeChange` which causes exception when using with `capture.mode:change_streams_update_full_with_pre_image` debezium config

`FullDocumentBeforeChange` only exist from `4.7.0` onwards

```log
Caused by: java.lang.ClassNotFoundException: com.mongodb.client.model.changestream.FullDocumentBeforeChange
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641) ~[na:na]
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188) ~[na:na]
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:520) ~[na:na]
        ... 14 common frames omitted
```

---

Not able to view before and after document when using with `change_streams_update_full_with_pre_image`. Possibly be due to using `MongoDB 5.x`. See [change-streams-mongodb-6-0-support-pre-post-image](https://www.mongodb.com/blog/post/change-streams-mongodb-6-0-support-pre-post-image-retrieval-ddl-operations) and [introducing-mongodb-connector-apache-kafka-version-1-9](https://www.mongodb.com/blog/post/introducing-mongodb-connector-apache-kafka-version-1-9)



Starting FileOffsetBackingStore with file C:\Users\Joseph\AppData\Local\Temp\offsets_5858600127625159299.dat


---

https://github.com/docker-library/mongo/issues/475
https://gist.github.com/zhangyoufu/d1d43ac0fa268cda4dd2dfe55a8c834e

mongo.dockerfile

openssl rand -base64 756 > <path-to-keyfile>
chmod 400 <path-to-keyfile>

---

how to convert debezium struct to pojo

- https://stackoverflow.com/questions/61287814/extract-and-transform-kafka-message-specific-fields-for-jdbc-sink-connector
- https://stackoverflow.com/questions/66384583/unable-to-deserialise-dynamic-json-with-jackson-using-generics
- https://debezium.io/blog/2020/02/19/debezium-camel-integration/

enable preandpostimage

- https://www.mongodb.com/docs/manual/reference/command/collMod/#change-streams-with-document-pre--and-post-images
- https://debezium.io/documentation/reference/2.1/connectors/mongodb.html#mongodb-pre-image-support

important to know about snapshot

- https://debezium.io/documentation/reference/2.1/connectors/mongodb.html#mongodb-performing-a-snapshot

---

When an event happens from `MongoDB`, it will be either a `READ, CREATE, UPDATE, or DELETE` event which will be captured, and what we will be interested in.

> Refer to [here](https://debezium.io/documentation/reference/2.1/connectors/mongodb.html#mongodb-create-events) for the various operations

> Note that the schema appears to be slighly different from website which this is not using Kafka

The raw record comes in the form of `RecordChangeEvent<SourceRecord>`

```log
"EmbeddedEngineChangeEvent [key=null, value=SourceRecord{sourcePartition={rs=atlas-b15fi5-shard-0, server_id=sbd-mongodb-connector}, sourceOffset={sec=1678541505, ord=1, transaction_id=null}} ConnectRecord{topic='sbd-mongodb-connector.source.person', kafkaPartition=null, key=Struct{id={"$oid": "640c82aa8675575f87333342"}}, keySchema=Schema{sbd-mongodb-connector.source.person.Key:STRUCT}, value=Struct{after={"_id": {"$oid": "640c82aa8675575f87333342"},"name": "joseph2","description": "hello world 2","hashTags": ["hello","world"],"_class": "com.bwgjoseph.springbootdebeziummongodbes.mongo.Person"},source=Struct{version=2.1.2.Final,connector=mongodb,name=sbd-mongodb-connector,ts_ms=1678541505000,snapshot=last,db=source,rs=atlas-b15fi5-shard-0,collection=person,ord=1},op=r,ts_ms=1678542158585}, valueSchema=Schema{sbd-mongodb-connector.source.person.Envelope:STRUCT}, timestamp=null, headers=ConnectHeaders(headers=)}, sourceRecord=SourceRecord{sourcePartition={rs=atlas-b15fi5-shard-0, server_id=sbd-mongodb-connector}, sourceOffset={sec=1678541505, ord=1, transaction_id=null}} ConnectRecord{topic='sbd-mongodb-connector.source.person', kafkaPartition=null, key=Struct{id={"$oid": "640c82aa8675575f87333342"}}, keySchema=Schema{sbd-mongodb-connector.source.person.Key:STRUCT}, value=Struct{after={"_id": {"$oid": "640c82aa8675575f87333342"},"name": "joseph2","description": "hello world 2","hashTags": ["hello","world"],"_class": "com.bwgjoseph.springbootdebeziummongodbes.mongo.Person"},source=Struct{version=2.1.2.Final,connector=mongodb,name=sbd-mongodb-connector,ts_ms=1678541505000,snapshot=last,db=source,rs=atlas-b15fi5-shard-0,collection=person,ord=1},op=r,ts_ms=1678542158585}, valueSchema=Schema{sbd-mongodb-connector.source.person.Envelope:STRUCT}, timestamp=null, headers=ConnectHeaders(headers=)}]"
```

If we assign `RecordChangeEvent<SourceRecord> = changeEvent`, then `changeEvent.record()` will give us

```log
SourceRecord@121 "SourceRecord{sourcePartition={rs=atlas-b15fi5-shard-0, server_id=sbd-mongodb-connector}, sourceOffset={sec=1678541505, ord=1, transaction_id=null}} ConnectRecord{topic='sbd-mongodb-connector.source.person', kafkaPartition=null, key=Struct{id={"$oid": "640c82aa8675575f87333342"}}, keySchema=Schema{sbd-mongodb-connector.source.person.Key:STRUCT}, value=Struct{after={"_id": {"$oid": "640c82aa8675575f87333342"},"name": "joseph2","description": "hello world 2","hashTags": ["hello","world"],"_class": "com.bwgjoseph.springbootdebeziummongodbes.mongo.Person"},source=Struct{version=2.1.2.Final,connector=mongodb,name=sbd-mongodb-connector,ts_ms=1678541505000,snapshot=last,db=source,rs=atlas-b15fi5-shard-0,collection=person,ord=1},op=r,ts_ms=1678542158585}, valueSchema=Schema{sbd-mongodb-connector.source.person.Envelope:STRUCT}, timestamp=null, headers=ConnectHeaders(headers=)}"
```

And `changeEvent.record().value()` gives us

```log
Struct@126 "Struct{after={"_id": {"$oid": "640c82aa8675575f87333342"},"name": "joseph2","description": "hello world 2","hashTags": ["hello","world"],"_class": "com.bwgjoseph.springbootdebeziummongodbes.mongo.Person"},source=Struct{version=2.1.2.Final,connector=mongodb,name=sbd-mongodb-connector,ts_ms=1678541505000,snapshot=last,db=source,rs=atlas-b15fi5-shard-0,collection=person,ord=1},op=r,ts_ms=1678542158585}"
```

And this is what we should be mostly interested in. In here, we need to extract the following

- Operation
- Collection
- POJO

Reading the POJO is slightly tricky here while dealing with `MongoDB v5 and v6` because `preAndPostImage` is only supported from [v6](https://www.mongodb.com/docs/manual/reference/command/collMod/#change-streams-with-document-pre--and-post-images) onwards.

Now, let's call `changeEvent.record().value()` - `envelop`. I beautify it, and it should look something like this

```json
{
    "after": {
        "_id": {
            "$oid": "640c82aa8675575f87333342"
        },
        "name": "joseph2",
        "description": "hello world 2",
        "hashTags": [
            "hello",
            "world"
        ],
        "_class": "com.bwgjoseph.springbootdebeziummongodbes.mongo.Person"
    },
    "source": {
        "version": "2.1.2.Final",
        "connector": "mongodb",
        "name": "sbd-mongodb-connector",
        "ts_ms": 1678541505000,
        "snapshot": last,
        "db": "source",
        "rs": "atlas-b15fi5-shard-0",
        "collection": "person",
        "ord": 1,
        "h": 1546547425148721999
    },
    "op": "r",
    "ts_ms": 1678542158585
}
```

So we need

- `op`
- `source.collection`
- `after`

Assuming that we only have multiple collection, we might need to handle it differently, especially on the deserialization portion.

To grab `op`

```java
// op=r

String op = envelop.getString("op");
Operation operation = Operation.forCode(op);
```

To grab `source.collection`

```java
// source=Struct{version=2.1.2.Final,connector=mongodb,name=sbd-mongodb-connector,ts_ms=1678541505000,snapshot=last,db=source,rs=atlas-b15fi5-shard-0,collection=person,ord=1}

Struct source = envelop.getStruct("source");
String collection = source.getString("collection");
```

To grab `after`, or rather, the message (data) we are interested in, we need to deserialize it

```java
// Struct{after={"_id": {"$oid": "640c82aa8675575f87333342"},"name": "joseph2","description": "hello world 2","hashTags": ["hello","world"],"_class": "com.bwgjoseph.springbootdebeziummongodbes.mongo.Person"}

Object record = envelop.get("after");
ObjectMapper objectMapper = new ObjectMapper();
Person person = objectMapper.readValue(record.toString(), Person.class);
```

Once we have all these, we can then index the document. Before that, I created a `StructWrapper` class to make my life easier.

---

It's possible that we want to deserialize `after` into domain object for manipulation. Let's assume the class is like such

```java
@Getter
@ToString
@SuperBuilder(toBuilder = true)
public class BaseRecord {
    @Id
    private String id;
}

@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@TypeAlias(value = "person")
@Document(collection = "persons")
public class Person extends BaseRecord {
    private String name;
    private String description;
    private List<String> hashTags;
}
```

And that we cannot change the class like adding Jackson annotation

Then we attempt to convert using

```java
public <T> T getRecord(Class<T> clazz) {
    // handle v5
    if (this.operation.equals(Operation.DELETE)) {
        return null;
    }

    String toDeser = this.envelop.get(this.getBeforeOrAfter()).toString();

    log.info("Attempting to convert to mongo clazz {}", clazz);

    return this.conversionService.convert(toDeser, clazz);
}

public class StructPersonConverter implements Converter<String, Person> {
    private final ObjectMapper objectMapper;

    @Override
    public Person convert(String data) {
        try {
            return objectMapper.readValue(data, Person.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
```

We will encounter the following exception

```log
2023-06-11 13:17:45.019  INFO 5364 --- [ool-22-thread-1] c.b.s.debezium.StructWrapper             : Attempting to convert to mongo clazz class com.bwgjoseph.springbootdebeziummongodbes.mongo.Person
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.bwgjoseph.springbootdebeziummongodbes.mongo.Person` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
 at [Source: (String)"{"_id": {"$oid": "648558f81b59841304250742"},"name": "joseph","description": "hello world","hashTags": ["hello","world"],"_class": "person"}"; line: 1, column: 2]
        at com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:67)
```

There's 2 way to solve this

1. Use `@NoArgsConstructor`

Annotate it in both subclass, and baseclass

2. Use `@Jacksonized`

First, see what does `@Jacksonized` do

> The @Jacksonized annotation is an add-on annotation for @Builder and @SuperBuilder. It automatically configures the generated builder class to be used by Jackson's deserialization. It only has an effect if present at a context where there is also a @Builder or a @SuperBuilder

As we are using `@Builder/@SuperBuilder`, this is good for us.

Annotate `@Jacksonized` in subclass will be sufficient

*Note if using `Mixin`, then have to configure

```java
// this
objectMapper.addMixIn(BaseRecord.BaseRecordBuilder.class, BaseRecordMixin.class);
// not this (use this when using @NoArgsConstructor)
objectMapper.addMixIn(BaseRecord.class, BaseRecordMixin.class);
```

*Note if using `@Jacksonized` in `Mixin`, it does not seem to work

Tried

```java
objectMapper.addMixIn(Person.PersonBuilder.class, PersonMixin.class);
objectMapper.addMixIn(Person.class, PersonMixin.class);
```