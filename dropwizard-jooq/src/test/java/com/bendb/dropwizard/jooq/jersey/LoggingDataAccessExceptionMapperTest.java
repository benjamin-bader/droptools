package com.bendb.dropwizard.jooq.jersey;

import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.sql.SQLException;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LoggingDataAccessExceptionMapperTest {

    Logger logger = mock();
    LoggingDataAccessExceptionMapper mapper = new LoggingDataAccessExceptionMapper(logger);

    @Test
    public void logsUnderlyingSQLException() {
        SQLException cause = new SQLException("BAR");
        DataAccessException e = new DataAccessException("FOO", cause);
        mapper.logException(0, e);

        verify(logger).error(anyString(), eq(cause));
    }

    @Test
    public void logsAllUnderlyingCauses() {
        SQLException one = new SQLException("a");
        SQLException two = new SQLException("b");
        SQLException e = new SQLException("fail");
        e.setNextException(one);
        e.setNextException(two);

        DataAccessException dae = new DataAccessException("moar fail", e);

        mapper.logException(0, dae);

        verify(logger).error(anyString(), eq(e));
        verify(logger).error(anyString(), eq(one));
        verify(logger).error(anyString(), eq(two));
    }

    @Test
    public void logsExceptionItselfIfNoSQLException() {
        DataAccessException e = new DataAccessException("BAZ");
        mapper.logException(0, e);

        verify(logger).error(anyString(), eq(e));
    }
}