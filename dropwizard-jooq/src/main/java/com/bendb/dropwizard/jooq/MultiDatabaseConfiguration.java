package com.bendb.dropwizard.jooq;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import java.util.SortedMap;

public interface MultiDatabaseConfiguration<T extends Configuration> {

    /**
     * Override to add multiple data source factories to the bundle, each distinguished by a unique name.
     *
     * @param configuration a Configuration
     * @return a SortedMap containing DataSourceFactories, with the key being a String name.
     */
    SortedMap<String,DataSourceFactory> getSecondaryDataSourceFactories(T configuration);
}
