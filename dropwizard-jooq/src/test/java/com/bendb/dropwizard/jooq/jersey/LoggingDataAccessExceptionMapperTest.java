package com.bendb.dropwizard.jooq.jersey;

import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.sql.SQLException;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoggingDataAccessExceptionMapperTest {
    @Mock Logger logger;

    LoggingDataAccessExceptionMapper mapper;

    @BeforeEach
    public void setup() {
        LoggingDataAccessExceptionMapper.setLogger(logger);

        mapper = new LoggingDataAccessExceptionMapper();
    }

    @AfterEach
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