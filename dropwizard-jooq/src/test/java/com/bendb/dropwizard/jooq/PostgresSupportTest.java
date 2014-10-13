package com.bendb.dropwizard.jooq;

import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostgresSupportTest {
    @Mock Field<String> field;
    @Mock DataType<String> dataType;

    @Before
    public void setup() {
        when(field.getDataType()).thenReturn(SQLDataType.CLOB);
        when(field.toString()).thenReturn("TEXT");
    }

    @Test
    @Ignore("Don't know how to properly test this :(")
    public void appliesArrayAggGroupingFunction() {
        Field<String[]> agg = PostgresSupport.arrayAgg(field);
        // ..then what?
    }
}