package com.bendb.dropwizard.jooq;

import com.google.common.collect.ImmutableSortedMap;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;

import java.util.SortedMap;

public interface JooqConfiguration<C extends Configuration> extends DatabaseConfiguration<C>,
                                                                    MultiDatabaseConfiguration<C> {

    JooqFactory getJooqFactory(C configuration);

    /**
     * Override this method to use multiple databases, with instances referenced by name.
     */
    default SortedMap<String,DataSourceFactory> getSecondaryDataSourceFactories(C configuration) {
        return ImmutableSortedMap.of();
    }
}
