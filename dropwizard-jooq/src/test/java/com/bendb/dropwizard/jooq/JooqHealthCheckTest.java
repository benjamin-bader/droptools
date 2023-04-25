package com.bendb.dropwizard.jooq;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.codahale.metrics.health.HealthCheck;
import org.jooq.Configuration;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Savepoint;

public class JooqHealthCheckTest {
    private String validationQuery = "this is a query";

    @Test
    public void isHealthyIfNoExceptionIsThrown() throws Exception {
        MockDataProvider mockDataProvider = new MockDataProvider() {
            @Override
            public MockResult[] execute(MockExecuteContext ctx) throws SQLException {
                return new MockResult[0];
            }
        };

        HealthCheck.Result result = runHealthCheck(mockDataProvider);
        assertThat(result.isHealthy(), is(true));
    }

    @Test
    public void isUnhealthyIfTransactionFails() throws Exception {
        MockDataProvider mockDataProvider = new MockDataProvider() {
            @Override
            public MockResult[] execute(MockExecuteContext ctx) throws SQLException {
                throw new SQLException("BOOM");
            }
        };

        DataAccessException e = Assertions.assertThrows(DataAccessException.class, () -> runHealthCheck(mockDataProvider));
        assertThat(e.getMessage(), containsString(validationQuery));
        assertThat(e.getMessage(), containsString("BOOM"));
    }

    private HealthCheck.Result runHealthCheck(MockDataProvider provider) throws Exception {
        MockConnection mockConnection = new MockConnection(provider) {
            @Override
            public Savepoint setSavepoint() throws SQLException {
                return new Savepoint() {
                    @Override
                    public int getSavepointId() throws SQLException {
                        return 0;
                    }

                    @Override
                    public String getSavepointName() throws SQLException {
                        return "savepoint";
                    }
                };
            }
        };

        Configuration configuration = new DefaultConfiguration().set(mockConnection);
        configuration.settings().setExecuteLogging(false);
        JooqHealthCheck healthCheck = new JooqHealthCheck(configuration, validationQuery);

        return healthCheck.check();
    }
}