package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import com.bwgjoseph.springbootdebeziummongodbes.partialdate.PartialLocalDate;

public class MongoPartialLocalDateReader implements Converter<Document, PartialLocalDate> {

    @Override
    public PartialLocalDate convert(Document source) {
        System.out.print("converting" + source);
        return new PartialLocalDate(source.getString(MongoPartialLocalDateWriter.DATE_FIELD));
    }

}
