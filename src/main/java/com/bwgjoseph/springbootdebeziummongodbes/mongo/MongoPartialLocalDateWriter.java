package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import com.bwgjoseph.springbootdebeziummongodbes.partialdate.PartialLocalDate;

public class MongoPartialLocalDateWriter implements Converter<PartialLocalDate, Document> {

    public static final String DATE_FIELD = "date";
    public static final String CLASSIFIER_FIELD = "classifier";
    public static final String YEAR_FIELD = "year";
    public static final String MONTH_FIELD = "month";
    public static final String DAY_FIELD = "day";

    @Override
    public Document convert(PartialLocalDate source) {
        Document document = new Document();
        document.put(DATE_FIELD, source.toString());
        document.put(CLASSIFIER_FIELD, source.getClassifier());
        document.put(YEAR_FIELD, source.getYearValue());
        document.put(MONTH_FIELD, source.getMonthValue());
        document.put(DAY_FIELD, source.getDayValue());

        return document;
    }

}
