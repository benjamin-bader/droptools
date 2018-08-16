package com.bendb.dropwizard.jooq;

import org.joda.time.DateTime;
import org.jooq.Converter;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

import static com.google.common.truth.Truth.assertThat;

public class JodaDateTimeConverterTest {
    private JodaDateTimeConverter converter;

    @Before
    public void setup() {
        converter = new JodaDateTimeConverter();
    }

    @Test
    public void roundTripFromTimestampWorks() {
        Timestamp ts = new Timestamp(12345L);
        DateTime dt = converter.from(ts);

        assertThat(converter.to(dt)).isEqualTo(ts);
    }

    @Test
    public void roundTripFromDateTimeWorks() {
        DateTime dt = DateTime.now();
        Timestamp ts = converter.to(dt);

        assertThat(converter.from(ts)).isEqualTo(dt);
    }

    @Test
    public void convertsFromTimestamp() {
        assertThat(converter.fromType()).is(Timestamp.class);
    }

    @Test
    public void convertsToDateTime() {
        assertThat(converter.toType()).is(DateTime.class);
    }

    @Test
    public void nullTimestampReturnsNullDateTime() {
        assertThat(converter.from(null)).isNull();
    }

    @Test
    public void nullDateTimeReturnsNullTimestamp() {
        assertThat(converter.to(null)).isNull();
    }
}