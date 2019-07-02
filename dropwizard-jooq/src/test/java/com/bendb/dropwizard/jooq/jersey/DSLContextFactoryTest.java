package com.bendb.dropwizard.jooq.jersey;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DSLContextFactoryTest {
    @Mock Configuration configuration = mock(Configuration.class);

    private DSLContextFactory factory;

    @Before
    public void setup() {
        factory = new DSLContextFactory(configuration);
    }

    @Test
    public void createsADSLContext() {
        assertThat(factory.provide()).isInstanceOf(DSLContext.class);
    }
}
