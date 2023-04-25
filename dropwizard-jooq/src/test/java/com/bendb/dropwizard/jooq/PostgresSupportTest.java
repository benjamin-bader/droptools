package com.bendb.dropwizard.jooq;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.jooq.Field;
import org.jooq.test.data.Table1;
import org.junit.jupiter.api.Test;

public class PostgresSupportTest {
    @Test
    public void appliesArrayAggGroupingFunction() {
        Field<Integer[]> agg = PostgresSupport.arrayAgg(Table1.FIELD_ID1);
        assertThat(agg.toString(), equalTo("array_agg(" + Table1.FIELD_ID1.toString() + ")"));
    }

    @Test
    public void stripsNullsFromArraysWhenRequested() {
        Field<Integer[]> agg = PostgresSupport.arrayAggNoNulls(Table1.FIELD_ID1);
        String columnName = Table1.FIELD_ID1.toString();
        assertThat(agg.toString(), equalTo("array_remove(array_agg(" + columnName + "), NULL)"));
    }

    @Test
    public void joinsStringsWithGivenDelimiter() {
        Field<String> agg = PostgresSupport.stringAgg(Table1.FIELD_NAME1, ":");
        String columnName = Table1.FIELD_NAME1.toString();
        assertThat(agg.toString(), equalTo("string_agg(" + columnName + ", ':')"));
    }
}