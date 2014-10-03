package com.bendb.dropwizard.jooq;

import com.bendb.dropwizard.jooq.jersey.DSLContextInjectableProvider;
import com.bendb.dropwizard.jooq.jersey.LoggingDataAccessExceptionMapper;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public abstract class JooqBundle<C extends Configuration> implements ConfiguredBundle<C>, JooqConfiguration<C> {
    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }

    @Override
    public void run(C configuration, Environment environment) throws Exception {
        final DataSourceFactory dataSourceFactory = getDataSourceFactory(configuration);
        final JooqFactory jooqFactory = getJooqFactory(configuration);
        final org.jooq.Configuration cfg = jooqFactory.build(environment, dataSourceFactory);

        final JooqHealthCheck healthCheck = new JooqHealthCheck(cfg, dataSourceFactory.getValidationQuery());
        environment.healthChecks().register("jooq", healthCheck);

        final DSLContextInjectableProvider provider = new DSLContextInjectableProvider(cfg);
        environment.jersey().register(provider);

        environment.jersey().register(new LoggingDataAccessExceptionMapper());
    }
}
