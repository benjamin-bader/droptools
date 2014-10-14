package com.bendb.dropwizard.jooq;

import org.joda.time.DateTime;
import org.jooq.Converter;
import org.junit.Test;

import java.sql.Timestamp;

import static com.google.common.truth.Truth.assertThat;

public class JodaDateTimeConverterTest {
    @Test
    public void roundTripFromTimestampWorks() {
        Converter<Timestamp, DateTime> converter = new JodaDateTimeConverter();
        Timestamp ts = new Timestamp(12345L);
        DateTime dt = converter.from(ts);

        assertThat(converter.to(dt)).isEqualTo(ts);
    }

    @Test
    public void roundTripFromDateTimeWorks() {
        Converter<Timestamp, DateTime> converter = new JodaDateTimeConverter();
        DateTime dt = DateTime.now();
        Timestamp ts = converter.to(dt);

        assertThat(converter.from(ts)).isEqualTo(dt);
    }

    @Test
    public void convertsFromTimestamp() {
        assertThat(new JodaDateTimeConverter().fromType()).is(Timestamp.class);
    }

    @Test
    public void convertsToDateTime() {
        assertThat(new JodaDateTimeConverter().toType()).is(DateTime.class);
    }
}