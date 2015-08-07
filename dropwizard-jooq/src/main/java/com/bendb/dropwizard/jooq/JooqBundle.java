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
        final SortedMap<String,DataSourceFactory> dataSourceFactoryMap = getDataSourceFactories(configuration);

        for (final Map.Entry<String,DataSourceFactory> dataSourceFactoryEntry : dataSourceFactoryMap.entrySet()) {
            final String name = dataSourceFactoryEntry.getKey();
            final DataSourceFactory dataSourceFactory = dataSourceFactoryEntry.getValue();

            final JooqFactory jooqFactory = getJooqFactory(configuration);
            final Configuration cfg = jooqFactory.build(environment, dataSourceFactory, name);
            final JooqHealthCheck healthCheck = new JooqHealthCheck(cfg, dataSourceFactory.getValidationQuery());

            environment.healthChecks().register(name, healthCheck);

            jooqFactoryConfigurationMap.put(name, cfg);
        }

        environment.jersey().register(new JooqBinder(jooqFactoryConfigurationMap));
        environment.jersey().register(new LoggingDataAccessExceptionMapper());
    }

    /**
     * Override this method to use a non-default jOOQ configuration.
     */
    @Override
    public JooqFactory getJooqFactory(C configuration) {
        return new JooqFactory();
    }

    /**
     * Override this method to use a single database.
     * Deprecated: use getDataSourceFactories instead.
     */
    @Deprecated
    @Override
    public DataSourceFactory getDataSourceFactory(C configuration) {
        return null;
    }

    /**
     * Override this method to use multiple databases, with instances referenced by name.
     */
    @Override
    public SortedMap<String,DataSourceFactory> getDataSourceFactories(C configuration) {
        final SortedMap<String,DataSourceFactory> dataSourceFactoryMap = new TreeMap<>();

        // calls deprecated method to ensure backwards compatibility
        dataSourceFactoryMap.put(DEFAULT_NAME, getDataSourceFactory(configuration));
        return dataSourceFactoryMap;
    }

    public Configuration getConfiguration() {
        return jooqFactoryConfigurationMap.values().stream().findFirst().orElse(null);
    }

    public Map<String,Configuration> getConfigurationMap() {
        return jooqFactoryConfigurationMap;
    }
}
