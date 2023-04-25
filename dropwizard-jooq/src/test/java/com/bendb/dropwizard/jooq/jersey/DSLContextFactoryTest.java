package com.bendb.dropwizard.jooq.jersey;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class DSLContextFactoryTest {
    private DSLContextFactory factory;

    @BeforeEach
    public void setup() {
        Configuration configuration1 = new DefaultConfiguration();
        factory = new DSLContextFactory(configuration1);
    }

    @Test
    public void createsADSLContext() {
        assertThat(factory.provide()).isInstanceOf(DSLContext.class);
    }
}
