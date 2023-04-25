package com.bendb.dropwizard.jooq.jersey;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

public class DSLContextFactoryTest {
    private DSLContextFactory factory;

    @Before
    public void setup() {
        Configuration configuration1 = new DefaultConfiguration();
        factory = new DSLContextFactory(configuration1);
    }

    @Test
    public void createsADSLContext() {
        assertThat(factory.provide()).isInstanceOf(DSLContext.class);
    }
}
