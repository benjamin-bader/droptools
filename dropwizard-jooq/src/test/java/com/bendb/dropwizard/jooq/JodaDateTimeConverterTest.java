package com.bendb.dropwizard.jooq;

import org.joda.time.DateTime;
import org.junit.Test;

import java.sql.Timestamp;

import static com.google.common.truth.Truth.assertThat;

public class JodaDateTimeConverterTest {
    @Test
    public void roundTripFromTimestampWorks() {
        JodaDateTimeConverter converter = new JodaDateTimeConverter();
        Timestamp ts = new Timestamp(12345L);
        DateTime dt = converter.from(ts);

        assertThat(converter.to(dt)).isEqualTo(ts);
    }

    @Test
    public void roundTripFromDateTimeWorks() {
        JodaDateTimeConverter converter = new JodaDateTimeConverter();
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