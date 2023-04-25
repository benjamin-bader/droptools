package com.bendb.dropwizard.jooq.jersey;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DSLContextFactoryTest {
    private DSLContextFactory factory;

    @BeforeEach
    public void setup() {
        Configuration configuration1 = new DefaultConfiguration();
        factory = new DSLContextFactory(configuration1);
    }

    @Test
    public void createsADSLContext() {
        Assertions.assertInstanceOf(DSLContext.class, factory.provide());
    }
}
