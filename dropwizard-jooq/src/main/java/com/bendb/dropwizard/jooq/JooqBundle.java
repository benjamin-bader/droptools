package com.bendb.dropwizard.jooq;

import com.bendb.dropwizard.jooq.jersey.JooqBinder;
import com.bendb.dropwizard.jooq.jersey.LoggingDataAccessExceptionMapper;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jooq.Configuration;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class JooqBundle<C extends io.dropwizard.Configuration>
        implements ConfiguredBundle<C>, JooqConfiguration<C> {

    private static final String DEFAULT_NAME = "jooq";

    private final SortedMap<String,Configuration> jooqFactoryConfigurationMap = new TreeMap<>();

    public void initialize(Bootstrap<?> bootstrap) {
        // No bootstrap-phase action required.
    }

    @Override
    public void run(C configuration, Environment environment) throws Exception {

        // configure primary data source factory
        configureDataSourceFactory(
                configuration,
                environment,
                primaryDataSourceName(),
                getDataSourceFactory(configuration));

        // configure secondary data source factories
        getSecondaryDataSourceFactories(configuration)
                .entrySet()
                .stream()
                .forEach(
                        e -> configureDataSourceFactory(
                                configuration,
                                environment,
                                e.getKey(),
                                e.getValue()
                        )
                );

        environment.jersey().register(new JooqBinder(jooqFactoryConfigurationMap));
        environment.jersey().register(new LoggingDataAccessExceptionMapper());
    }

    /**
     * Override this method to change the default data source name.
     */
    public String primaryDataSourceName() {
        return DEFAULT_NAME;
    }

    /**
     * Override this method to use a non-default jOOQ configuration.
     */
    @Override
    public JooqFactory getJooqFactory(C configuration) {
        return new JooqFactory();
    }

    public Configuration getConfiguration() {
        return jooqFactoryConfigurationMap.values().stream().findFirst().orElse(null);
    }

    public Map<String,Configuration> getConfigurationMap() {
        return jooqFactoryConfigurationMap;
    }

    private void configureDataSourceFactory(
            final C configuration,
            final Environment environment,
            final String name,
            final DataSourceFactory dataSourceFactory
    ) throws RuntimeException {

        try {
            final JooqFactory jooqFactory = getJooqFactory(configuration);
            final Configuration cfg = jooqFactory.build(environment, dataSourceFactory, name);
            final JooqHealthCheck healthCheck = new JooqHealthCheck(cfg, dataSourceFactory.getValidationQuery());

            environment.healthChecks().register(name, healthCheck);

            jooqFactoryConfigurationMap.put(name, cfg);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
