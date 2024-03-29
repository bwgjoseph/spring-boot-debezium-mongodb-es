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

The above is my mistake, it is spring-boot that brings in the older driver, I just need to override in `pom.xml`

```xml
<properties>
		<!--
			Spring 2.7.12 brings in mongo 4.6.1 but debezium relies on 4.7.1
			Manually define to prevent debezium from using 4.6.1
			See https://issues.redhat.com/browse/DBZ-6181
		-->
		<mongodb.version>4.7.1</mongodb.version>
	</properties>
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

---

Unable to deserialize when using `LocalDateTime` and `Instant`

```log
2023-06-11 14:00:52.776  INFO 5364 --- [ool-78-thread-1] c.b.s.debezium.StructWrapper             : Attempting to convert to mongo clazz class com.bwgjoseph.springbootdebeziummongodbes.mongo.Person
com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize value of type `java.time.LocalDateTime` from Object value (token `JsonToken.START_OBJECT`)
 at [Source: (String)"{"_id": {"$oid": "648563141b59841304250815"},"name": "joseph","description": "hello world","hashTags": ["hello","world"],"createdAt": {"$date": 1686463252556},"updatedAt": {"$date": 1686463252556},"_class": "person"}"; line: 1, column: 135] (through reference chain: com.bwgjoseph.springbootdebeziummongodbes.mongo.Person$PersonBuilderImpl["createdAt"])
        at com.fasterxml.jackson.databind.exc.MismatchedInputException.from(MismatchedInputException.java:59)
        at com.fasterxml.jackson.databind.DeserializationContext.reportInputMismatch(DeserializationContext.java:1741)
```

To do that we create `LocalDateTimeDeserializer` to handle it like how we did for `ObjectIdDerserializer`

If we look at

```json
{
    "createdAt": {
        "$date": 1686463252556 // this is epoch time
    }
}
```

MongoDB stores in epoch time, and hence, we need to convert to `LocalDateTime`

This is the same as `Instant`

But do note a small difference

In Mongo, it is stored as

```json
{
  "_id": {
    "$oid": "648568d11b59841304250875"
  },
  "name": "joseph",
  "description": "hello world",
  "hashTags": [
    "hello",
    "world"
  ],
  "createdAt": {
    "$date": {
      "$numberLong": "1686464721711"
    }
  },
  "updatedAt": {
    "$date": {
      "$numberLong": "1686464721711"
    }
  },
  "occurredAt": {
    "$date": {
      "$numberLong": "1686464721712"
    }
  },
  "_class": "person"
}
```

```
createdAt
2023-06-11T06:25:21.711+00:00
updatedAt
2023-06-11T06:25:21.711+00:00
occurredAt
2023-06-11T06:25:21.712+00:00
```

However, after converted to its respective class

```java
Person(super=BaseRecord(id=648568d11b59841304250875, createdAt=2023-06-11T14:25:21.711, updatedAt=2023-06-11T14:25:21.711, occurredAt=2023-06-11T06:25:21.712Z), name=joseph, description=hello world, hashTags=[hello, world])
```

Notice that for `LocalDateTime`, it will automatically be shown as current (localdatetime) - `2023-06-11T14:25:21.711` but for `Instant`, it is defined as `UTC+0` using `2023-06-11T06:25:21.712Z` < notice the Z, and it's 06 instead of 14 >

---

Unable to Deserialize when have abstract class - `Source`

`Source` comes with 2 subclass; `InternalSource` and `ExternalSource`

An example of the data

```log
envelop Struct{after={"_id": {"$oid": "6485740d224921287a3875bb"},"name": "joseph","description": "hello world","hashTags": ["hello","world"],"createdAt": {"$date": 1686467597086},"updatedAt": {"$date": 1686467597086},"occurredAt": {"$date": 1686467597089},"sources": [{"internal": "internal","sourceType": "INTERNAL","obtainedAt": {"$date": 1686467597086},"remarks": "internal remarks","_class": "com.bwgjoseph.springbootdebeziummongodbes.mongo.InternalSource"},{"external": "external","sourceType": "EXTERNAL","obtainedAt": {"$date": 1686467597086},"remarks": "external remarks","_class": "com.bwgjoseph.springbootdebeziummongodbes.mongo.ExternalSource"}],"_class": "person"},source=Struct{version=2.1.2.Final,connector=mongodb,name=dbz,ts_ms=1686467599000,db=source,rs=esrs,collection=persons,ord=1},op=c,ts_ms=1686467599150}
```

Exception when trying to deserialize

```log
2023-06-11 15:13:19.657  INFO 25564 --- [pool-2-thread-1] c.b.s.debezium.StructWrapper             : Attempting to convert to mongo clazz class com.bwgjoseph.springbootdebeziummongodbes.mongo.Person
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.bwgjoseph.springbootdebeziummongodbes.mongo.Source` (no Creators, like default constructor, exist): abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information
 at [Source: (String)"{"_id": {"$oid": "6485740d224921287a3875bb"},"name": "joseph","description": "hello world","hashTags": ["hello","world"],"createdAt": {"$date": 1686467597086},"updatedAt": {"$date": 1686467597086},"occurredAt": {"$date": 1686467597089},"sources": [{"internal": "internal","sourceType": "INTERNAL","obtainedAt": {"$date": 1686467597086},"remarks": "internal remarks","_class": "com.bwgjoseph.springbootdebeziummongodbes.mongo.InternalSource"},{"external": "external","sourceType": "EXTERNAL","obtained"[truncated 156 chars]; line: 1, column: 249] (through reference chain: com.bwgjoseph.springbootdebeziummongodbes.mongo.Person$PersonBuilderImpl["sources"]->java.util.ArrayList[0])
        at com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:67)
        at com.fasterxml.jackson.databind.DeserializationContext.reportBadDefinition(DeserializationContext.java:1904)
```

So we want to make sure that it works using the normal means first before moving on to see if we can use `Mixin`, etc to get it working

In `Source` abstract class, add `@JsonTypeInfo, @JsonSubTypes` to tell Jackson on how to handle polymorphic deserialization

```java
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "sourceType", visible = true)
@JsonSubTypes({
    @Type(value = InternalSource.class, name = "INTERNAL"),
    @Type(value = ExternalSource.class, name = "EXTERNAL"),
})
public abstract class Source {
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime obtainedAt;
}
```

And because we have `LocalDateTime` here, we need to add `@JsonDeserialize` too. And don't forget that we need to have `@Jacksonized` annotation on each subclass as well.

With that, it will be able to deserialize correctly

```log
2023-06-11 15:36:56.435  INFO 8688 --- [ool-26-thread-1] c.b.s.debezium.StructWrapper             : Attempting to convert to mongo clazz class com.bwgjoseph.springbootdebeziummongodbes.mongo.Person
2023-06-11 15:36:56.444  INFO 8688 --- [ool-26-thread-1] c.b.s.d.DebeziumSourceEventListenerV5    : mongo record Person(super=BaseRecord(id=64857997147cdd3661944ee3, createdAt=2023-06-11T15:36:55.016, updatedAt=2023-06-11T15:36:55.016, occurredAt=2023-06-11T07:36:55.018Z, sources=[InternalSource(super=Source(sourceType=INTERNAL, obtainedAt=2023-06-11T15:36:55.016, remarks=internal remarks), internal=internal), ExternalSource(super=Source(sourceType=EXTERNAL, obtainedAt=2023-06-11T15:36:55.016, remarks=external remarks), external=external)]), name=joseph, description=hello world, hashTags=[hello, world])
```

Now, with that, let's see if we can spice things up a little by going back to the principle where we cannot change our domain object

Let's try that with `Mixin`

```java
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "sourceType", visible = true)
@JsonSubTypes({
    @Type(value = InternalSource.class, name = "INTERNAL"),
    @Type(value = ExternalSource.class, name = "EXTERNAL"),
})
public abstract class SourceMixin {
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime obtainedAt;
}
```

Then in our `StructPersonConverter` class, we declare the `Mixin`

```java
objectMapper.addMixIn(Source.SourceBuilder.class, SourceMixin.class);
```

However, it does not work

```log
2023-06-11 15:43:29.714  INFO 8688 --- [ool-34-thread-1] c.b.s.debezium.StructWrapper             : Attempting to convert to mongo clazz class com.bwgjoseph.springbootdebeziummongodbes.mongo.Person
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.bwgjoseph.springbootdebeziummongodbes.mongo.Source` (no Creators, like default constructor, exist): abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information
 at [Source: (String)"{"_id": {"$oid": "64857b21147cdd3661944f01"},"name": "joseph","description": "hello world","hashTags": ["hello","world"],"createdAt": {"$date": 1686469409136},"updatedAt": {"$date": 1686469409136},"occurredAt": {"$date": 1686469409142},"sources": [{"internal": "internal","sourceType": "INTERNAL","obtainedAt": {"$date": 1686469409136},"remarks": "internal remarks","_class": "com.bwgjoseph.springbootdebeziummongodbes.mongo.InternalSource"},{"external": "external","sourceType": "EXTERNAL","obtained"[truncated 156 chars]; line: 1, column: 249] (through reference chain: com.bwgjoseph.springbootdebeziummongodbes.mongo.Person$PersonBuilderImpl["sources"]->java.util.ArrayList[0])
        at com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:67)
```

It seem like `Mixin` does not read annotation from the class level? After some quick research, it seem that it does support. https://github.com/FasterXML/jackson-databind/issues/2425

So what happened?

The `Mixin` should be applied to the `Source` class and not `Source.SourceBuilder` class

```java
objectMapper.addMixIn(Source.class, SourceMixin.class);
```

If we do that, and run, we will encounter another exception

```log
2023-06-11 16:12:51.292  INFO 7024 --- [pool-6-thread-1] c.b.s.debezium.StructWrapper             : Attempting to convert to mongo clazz class com.bwgjoseph.springbootdebeziummongodbes.mongo.Person
com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize value of type `java.time.LocalDateTime` from Object value (token `JsonToken.START_OBJECT`)
 at [Source: (String)"{"_id": {"$oid": "6485820205359759e2e464ae"},"name": "joseph","description": "hello world","hashTags": ["hello","world"],"createdAt": {"$date": 1686471170685},"updatedAt": {"$date": 1686471170685},"occurredAt": {"$date": 1686471170692},"sources": [{"internal": "internal","sourceType": "INTERNAL","obtainedAt": {"$date": 1686471170685},"remarks": "internal remarks","_class": "com.bwgjoseph.springbootdebeziummongodbes.mongo.InternalSource"},{"external": "external","sourceType": "EXTERNAL","obtained"[truncated 156 chars]; line: 1, column: 312] (through reference chain: com.bwgjoseph.springbootdebeziummongodbes.mongo.Person$PersonBuilderImpl["sources"]->java.util.ArrayList[0]->com.bwgjoseph.springbootdebeziummongodbes.mongo.InternalSource$InternalSourceBuilderImpl["obtainedAt"])
        at com.fasterxml.jackson.databind.exc.MismatchedInputException.from(MismatchedInputException.java:59)
        at com.fasterxml.jackson.databind.DeserializationContext.reportInputMismatch(DeserializationContext.java:1741)
```

This is weird, it should have worked given that we already declared `@JsonDeserialize` for `obtainedAt` field. While I can't quite figure out the exact reason, but it's something like registering `SourceMixin` onto `Source.class` tells `Jackson` how to deserialize the subclass correctly via `@JsonTypeInfo` but for the concrete class, it has to deserialize the field `obtainedAt`, and that needs to come by adding `Mixin` to `Source.SourceBuilder.class` instead of `Source.class`

So in short, it means that we need one `Mixin` class to tell Jackson how to deserialize the field and another `Mixin` class to tell Jackson how to deserialize the polymorphic class. And we will be left with

```java
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "sourceType", visible = true)
@JsonSubTypes({
    @Type(value = InternalSource.class, name = "INTERNAL"),
    @Type(value = ExternalSource.class, name = "EXTERNAL"),
})
public abstract class SourceMixin {}

public abstract class SourceFieldMixin {
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime obtainedAt;
}

// StructPersonConverter
objectMapper.addMixIn(Source.class, SourceMixin.class);
objectMapper.addMixIn(Source.SourceBuilder.class, SourceFieldMixin.class);
```

---

Next, I want to explore if we can simplify some deserialization rule by registering a global deserialization handler for certain data type like `LocalDateTime, Instant` rather than having to use `Mixin` and annotate one by one which is quite troublesome

And that's rather straightforward. We just need to register it as a module and we are good to go!

```java
SimpleModule simpleModule = new SimpleModule();
simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
simpleModule.addDeserializer(Instant.class, new InstantDeserializer());
objectMapper.registerModule(simpleModule);
```

Once we register it, we can remove all the individual `@JsonDeserialize` declaration across the various `Mixins`. Unfortunately, I'm not sure if there's a good way for `ObjectIdDeserializer` because it's a `String` object, and if register as a global option, that would be too overwhelming. Unless I use `ObjectId` as the data type for `_id` within the `BaseRecord` class, if not, then using `@JsonDeserialize` is still the best option.

Of course, do note that by registering it as the global deserialize, the drawback of it is that it does it for the object every single time unlike `@JsonDeserializer` where you can indicate when you want to "activate" it.

---

Now, we add in `PartialLocalDate`, and see if we can achieve easily deserialize via the previous methods that we have explored. For this, we gonna want to register the global deserializer as well.

Without the custom deserializer, we will encounter similar error

```log
2023-06-11 17:08:46.977  INFO 27028 --- [pool-2-thread-1] c.b.s.debezium.StructWrapper             : Attempting to convert to mongo clazz class com.bwgjoseph.springbootdebeziummongodbes.mongo.Person
com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of `com.bwgjoseph.springbootdebeziummongodbes.partialdate.PartialLocalDate` (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator)
 at [Source: (String)"{"_id": {"$oid": "64858f1e137292513a101382"},"name": "joseph","description": "hello world","hashTags": ["hello","world"],"dob": {"date": "2023-06-11","classifier": "LOCAL_DATE","year": 2023,"month": 6,"day": 11},"createdAt": {"$date": 1686474525883},"updatedAt": {"$date": 1686474525883},"occurredAt":
{"$date": 1686474525901},"sources": [{"internal": "internal","sourceType": "INTERNAL","obtainedAt": {"$date": 1686474525883},"remarks": "internal remarks","_class": "source.internal"},{"external": ""[truncated 153 chars]; line: 1, column: 130] (through reference chain: com.bwgjoseph.springbootdebeziummongodbes.mongo.Person$PersonBuilderImpl["dob"])
        at com.fasterxml.jackson.databind.exc.MismatchedInputException.from(MismatchedInputException.java:63)
```

So, let's create one, and then register it, and see if it works

And viola! it does!

```log
2023-06-11 17:14:15.084  INFO 8876 --- [pool-2-thread-1] c.b.s.debezium.StructWrapper             : Attempting to convert to mongo clazz class com.bwgjoseph.springbootdebeziummongodbes.mongo.Person
2023-06-11 17:14:15.118  INFO 8876 --- [pool-2-thread-1] c.b.s.d.DebeziumSourceEventListenerV5    : mongo record Person(super=BaseRecord(id=6485906645842b7e0b8899b2, createdAt=2023-06-11T17:14:14.081, updatedAt=2023-06-11T17:14:14.081, occurredAt=2023-06-11T09:14:14.087Z, sources=[InternalSource(super=Source(sourceType=INTERNAL, obtainedAt=2023-06-11T17:14:14.081, remarks=internal remarks), internal=internal), ExternalSource(super=Source(sourceType=EXTERNAL, obtainedAt=2023-06-11T17:14:14.081, remarks=external remarks), external=external)]), name=joseph, description=hello world, hashTags=[hello, world], dob=2023-06-11)
```

---

What happens if it's 3rd party object like `GeoJsonPoint`? Well, interesting, there's nothing that we need to do

```json
{
  "_id": {
    "$oid": "648591a89d365116db685179"
  },
  "name": "joseph",
  "description": "hello world",
  "hashTags": [
    "hello",
    "world"
  ],
  "dob": {
    "date": "2023-06-11",
    "classifier": "LOCAL_DATE",
    "year": 2023,
    "month": 6,
    "day": 11
  },
  "location": {
    "type": "Point",
    "coordinates": [
      100.3,
      1.34
    ]
  },
  "createdAt": {
    "$date": {
      "$numberLong": "1686475176329"
    }
  },
  "updatedAt": {
    "$date": {
      "$numberLong": "1686475176329"
    }
  },
  "occurredAt": {
    "$date": {
      "$numberLong": "1686475176334"
    }
  },
  "sources": [
    {
      "internal": "internal",
      "sourceType": "INTERNAL",
      "obtainedAt": {
        "$date": {
          "$numberLong": "1686475176329"
        }
      },
      "remarks": "internal remarks",
      "_class": "source.internal"
    },
    {
      "external": "external",
      "sourceType": "EXTERNAL",
      "obtainedAt": {
        "$date": {
          "$numberLong": "1686475176329"
        }
      },
      "remarks": "external remarks",
      "_class": "source.external"
    }
  ],
  "_class": "person"
}
```

It works out of the box

```log
2023-06-11 17:19:36.904  INFO 21904 --- [pool-2-thread-1] c.b.s.debezium.StructWrapper             : Attempting to convert to mongo clazz class com.bwgjoseph.springbootdebeziummongodbes.mongo.Person
2023-06-11 17:19:37.048  INFO 21904 --- [pool-2-thread-1] c.b.s.d.DebeziumSourceEventListenerV5    : mongo record Person(super=BaseRecord(id=648591a89d365116db685179, createdAt=2023-06-11T17:19:36.329, updatedAt=2023-06-11T17:19:36.329, occurredAt=2023-06-11T09:19:36.334Z, sources=[InternalSource(super=Source(sourceType=INTERNAL, obtainedAt=2023-06-11T17:19:36.329, remarks=internal remarks), internal=internal), ExternalSource(super=Source(sourceType=EXTERNAL, obtainedAt=2023-06-11T17:19:36.329, remarks=external remarks), external=external)]), name=joseph, description=hello world, hashTags=[hello, world], dob=2023-06-11, location=Point [x=100.300000, y=1.340000])
```
