package com.bendb.dropwizard.jooq;

import com.bendb.dropwizard.jooq.jersey.JooqBinder;
import com.bendb.dropwizard.jooq.jersey.LoggingDataAccessExceptionMapper;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jooq.Configuration;

public abstract class JooqBundle<C extends io.dropwizard.Configuration>
        implements ConfiguredBundle<C>, JooqConfiguration<C> {
    private Configuration configuration;

    public void initialize(Bootstrap<?> bootstrap) {
        // No bootstrap-phase action required.
    }

    @Override
    public void run(C configuration, Environment environment) throws Exception {
        final DataSourceFactory dataSourceFactory = getDataSourceFactory(configuration);
        final JooqFactory jooqFactory = getJooqFactory(configuration);
        final Configuration cfg = jooqFactory.build(environment, dataSourceFactory);
        final JooqHealthCheck healthCheck = new JooqHealthCheck(cfg, dataSourceFactory.getValidationQuery());

        environment.healthChecks().register("jooq", healthCheck);
        environment.jersey().register(new JooqBinder(cfg));
        environment.jersey().register(new LoggingDataAccessExceptionMapper());

        this.configuration = cfg;
    }

    @Override
    public JooqFactory getJooqFactory(C configuration) {
        // Override this method to use a non-default jOOQ configuration.
        return new JooqFactory();
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
