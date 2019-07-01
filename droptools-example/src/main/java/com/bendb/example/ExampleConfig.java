package com.bendb.example;

import com.bendb.dropwizard.jooq.JooqFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayFactory;

import javax.validation.constraints.NotNull;

public class ExampleConfig extends Configuration {
    @JsonProperty
    @NotNull
    private FlywayFactory flyway;

    @JsonProperty
    @NotNull
    private JooqFactory jooq = new JooqFactory(); // Defaults are acceptable

    @JsonProperty
    @NotNull
    private DataSourceFactory databasePrimary;

    @JsonProperty
    @NotNull
    private DataSourceFactory databaseReplica;

    public FlywayFactory flyway() {
        return flyway;
    }

    public JooqFactory jooq() {
        return jooq;
    }

    public DataSourceFactory dataSourceFactoryPrimary() {
        return databasePrimary;
    }

    public DataSourceFactory dataSourceFactoryReplica() {
        return databaseReplica;
    }
}
