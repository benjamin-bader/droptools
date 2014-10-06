package com.bendb.dropwizard.jooq.jersey;

import com.google.common.annotations.VisibleForTesting;
import io.dropwizard.jersey.errors.LoggingExceptionMapper;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.Provider;
import java.sql.SQLException;

@Provider
public class LoggingDataAccessExceptionMapper extends LoggingExceptionMapper<DataAccessException> {
    private static Logger logger = LoggerFactory.getLogger(LoggingDataAccessExceptionMapper.class);

    @Override
    protected void logException(long id, DataAccessException exception) {
        final Throwable cause = exception.getCause();
        if (cause instanceof SQLException) {
            for (Throwable throwable : (SQLException) cause) {
                logger.error(formatLogMessage(id, throwable), throwable);
            }
        } else {
            logger.error(formatLogMessage(id, exception), exception);
        }
    }

    @VisibleForTesting
    static void setLogger(Logger logger) {
        LoggingDataAccessExceptionMapper.logger = logger;
    }
}
