package com.bendb.dropwizard.jooq.jersey;

import org.jooq.exception.DataAccessException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.sql.SQLException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoggingDataAccessExceptionMapperTest {
    @Mock Logger logger;

    LoggingDataAccessExceptionMapper mapper;

    @Before
    public void setup() {
        LoggingDataAccessExceptionMapper.setLogger(logger);

        mapper = new LoggingDataAccessExceptionMapper();
    }

    @After
    public void tearDown() {
        LoggingDataAccessExceptionMapper.setLogger(null);
    }

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