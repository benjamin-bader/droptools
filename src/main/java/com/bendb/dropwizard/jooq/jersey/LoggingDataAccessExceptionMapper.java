package com.bendb.dropwizard.jooq.jersey;

import io.dropwizard.jersey.errors.LoggingExceptionMapper;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.Provider;
import java.sql.SQLException;

@Provider
public class LoggingDataAccessExceptionMapper extends LoggingExceptionMapper<DataAccessException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingDataAccessExceptionMapper.class);

    @Override
    protected void logException(long id, DataAccessException exception) {
        final Throwable cause = exception.getCause();
        if (cause instanceof SQLException) {
            for (Throwable throwable : (SQLException) cause) {
                LOGGER.error(formatLogMessage(id, throwable), throwable);
            }
        } else {
            LOGGER.error(formatLogMessage(id, exception), exception);
        }
    }
}
