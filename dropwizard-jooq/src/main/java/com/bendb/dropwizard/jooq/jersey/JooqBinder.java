package com.bendb.dropwizard.jooq.jersey;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;

/**
 * Create bindings for {@link DSLContext} (via
 * {@link com.bendb.dropwizard.jooq.jersey.DSLContextFactory}),
 * {@link org.jooq.Configuration}, and {@link org.jooq.ConnectionProvider}.
 */
public class JooqBinder extends AbstractBinder {

    private final Configuration configuration;

    public JooqBinder(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bindFactory(new DSLContextFactory(configuration))
                .to(DSLContext.class)
                .in(RequestScoped.class);

        // Configuration and ConnectionProvider are single instance, used everywhere

        bind(configuration).to(Configuration.class);

        bind(configuration.connectionProvider())
                .to(ConnectionProvider.class);
    }
}
