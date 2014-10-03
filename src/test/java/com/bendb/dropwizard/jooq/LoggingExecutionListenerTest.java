package com.bendb.dropwizard.jooq;

import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.Query;
import org.jooq.Routine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.sql.PreparedStatement;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoggingExecutionListenerTest {
    @Mock Logger logger;
    @Mock DSLContext dbContext;
    @Mock ExecuteContext context;
    @Mock Query query;
    @Mock Routine<?> routine;

    private LoggingExecutionListener listener;

    @Before
    public void setup() {
        LoggingExecutionListener.setLogger(logger);

        listener = new LoggingExecutionListener(dbContext);
    }

    @After
    public void tearDown() {
        LoggingExecutionListener.setLogger(null);
    }

    @Test
    public void logsQueries() {
        when(context.query()).thenReturn(query);
        when(dbContext.renderInlined(query)).thenReturn("SELECT * FROM USERS");

        listener.executeStart(context);

        verify(logger).info("SELECT * FROM USERS");
    }

    @Test
    public void doesNotLogSqlWhenQueryIsDefined() {
        when(context.query()).thenReturn(query);
        when(dbContext.renderInlined(query)).thenReturn("query");
        when(context.sql()).thenReturn("sql");

        listener.executeStart(context);

        verify(logger, never()).info("sql");
    }

    @Test
    public void doesNotLogSqlWhenRoutineIsDefined() {
        doReturn(routine).when(context).routine();
        when(dbContext.renderInlined(routine)).thenReturn("routine");
        when(context.sql()).thenReturn("sql");

        listener.executeStart(context);

        verify(logger, never()).info("sql");
    }

    @Test
    public void logsRoutines() {
        doReturn(routine).when(context).routine();
        when(dbContext.renderInlined(routine)).thenReturn("SELECT dbo.SomeFunction()");

        listener.executeStart(context);

        verify(logger).info("SELECT dbo.SomeFunction()");
    }

    @Test
    public void logsSqlWhenQueryAndRoutineAreNull() {
        when(context.sql()).thenReturn("SELECT COUNT(*)");

        listener.executeStart(context);

        verify(logger).info("SELECT COUNT(*)");
    }

    @Test
    public void doesNothingWhenNoLoggableDataIsFound() {
        listener.executeStart(context);
        verify(logger, never()).info(anyString());
    }
}