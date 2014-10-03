package com.bendb.dropwizard.jooq;

import org.joda.time.DateTime;
import org.jooq.Converter;

import java.sql.Timestamp;

/**
 * A {@link org.jooq.Converter} for {@link org.joda.time.DateTime} objects.
 */
public class JodaDateTimeConverter implements Converter<Timestamp, DateTime> {
    @Override
    public DateTime from(Timestamp timestamp) {
        return new DateTime(timestamp.getTime());
    }

    @Override
    public Timestamp to(DateTime dateTime) {
        return new Timestamp(dateTime.getMillis());
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<DateTime> toType() {
        return DateTime.class;
    }
}
