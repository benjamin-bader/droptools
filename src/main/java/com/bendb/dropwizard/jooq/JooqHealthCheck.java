package com.bendb.dropwizard.jooq;

import com.codahale.metrics.health.HealthCheck;
import org.jooq.Configuration;
import org.jooq.TransactionalRunnable;
import org.jooq.impl.DSL;

public class JooqHealthCheck extends HealthCheck {
    private final Configuration configuration;
    private final String validationQuery;

    public JooqHealthCheck(Configuration configuration, String validationQuery) {
        this.configuration = configuration;
        this.validationQuery = validationQuery;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    @Override
    protected Result check() throws Exception {
        DSL.using(configuration).transaction(new TransactionalRunnable() {
            @Override
            public void run(Configuration configuration) throws Exception {
                DSL.using(configuration).execute(validationQuery);
            }
        });

        return Result.healthy();
    }
}
