package com.bendb.example;

import com.bendb.dropwizard.jooq.JooqBundle;
import com.bendb.dropwizard.jooq.JooqFactory;
import com.bendb.example.resources.PostsResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ExampleApp extends Application<ExampleConfig> {
    public static void main(String[] args) throws Exception {
        new ExampleApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<ExampleConfig> bootstrap) {
        bootstrap.addBundle(new FlywayBundle<ExampleConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ExampleConfig configuration) {
                return configuration.dataSourceFactory();
            }

            @Override
            public FlywayFactory getFlywayFactory(ExampleConfig configuration) {
                return configuration.flyway();
            }
        });

        bootstrap.addBundle(new JooqBundle<ExampleConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ExampleConfig configuration) {
                return configuration.dataSourceFactory();
            }

            @Override
            public JooqFactory getJooqFactory(ExampleConfig configuration) {
                return configuration.jooq();
            }
        });
    }

    @Override
    public void run(ExampleConfig configuration, Environment environment) throws Exception {
        environment.jersey().register(PostsResource.class);
    }
}
