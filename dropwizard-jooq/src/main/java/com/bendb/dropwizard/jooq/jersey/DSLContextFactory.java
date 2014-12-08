package com.bendb.dropwizard.jooq.jersey;

import org.glassfish.hk2.api.Factory;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

/**
 * Bind
 */
public class DSLContextFactory implements Factory<DSLContext> {
    private final Configuration configuration;

    public DSLContextFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public DSLContext provide() {
        return DSL.using(configuration);
    }

    @Override
    public void dispose(DSLContext instance) {
    }
}
