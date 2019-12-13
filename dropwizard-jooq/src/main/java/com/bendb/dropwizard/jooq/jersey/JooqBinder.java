package com.bendb.dropwizard.jooq.jersey;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;

import java.util.SortedMap;

/**
 * Create bindings for {@link DSLContext} (via
 * {@link com.bendb.dropwizard.jooq.jersey.DSLContextFactory}),
 * {@link org.jooq.Configuration}, and {@link org.jooq.ConnectionProvider}.
 */
public class JooqBinder extends AbstractBinder {

    private final SortedMap<String, Configuration> configurationMap;

    public JooqBinder(final SortedMap<String, Configuration> configurationMap) {
        this.configurationMap = configurationMap;
    }

    @Override
    protected void configure() {
        // bind default Configuration to DSLContext
        bindFactory(new DSLContextFactory(configurationMap.values().stream().findFirst().orElse(null)))
                .to(DSLContext.class)
                .in(RequestScoped.class);

        // bind multiple instances of Configuration and ConnectionProvider for Named DSLContext(s)
        for (final Configuration configuration : configurationMap.values()) {

            bind(configuration).to(Configuration.class);

            bind(configuration.connectionProvider())
                    .to(ConnectionProvider.class);
        }

        // bind a ValueParamProvider for Named DSLContext(s)
        bind(new DSLContextValueParamProvider(configurationMap))
                .to(ValueParamProvider.class);
    }
}
