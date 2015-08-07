package com.bendb.dropwizard.jooq;

import io.dropwizard.Configuration;
import io.dropwizard.db.DatabaseConfiguration;

public interface JooqConfiguration<C extends Configuration> extends DatabaseConfiguration<C>,
                                                                    MultiDatabaseConfiguration<C> {
    JooqFactory getJooqFactory(C configuration);
}
