package com.bendb.dropwizard.jooq;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.impl.DefaultExecuteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExecutionListener extends DefaultExecuteListener {
    private static Logger logger = LoggerFactory.getLogger("jooq.execution");

    private final DSLContext create;

    public LoggingExecutionListener(DSLContext create) {
        this.create = create;
    }

    @Override
    public void executeStart(ExecuteContext ctx) {
        String statement = null;
        if (ctx.query() != null) {
            statement = create.renderInlined(ctx.query());
        } else if (ctx.routine() != null) {
            statement = create.renderInlined(ctx.routine());
        } else if (!Strings.isNullOrEmpty(ctx.sql())) {
            statement = ctx.sql();
        }

        if (statement != null) {
            logger.info(statement);
        }
    }

    @VisibleForTesting
    static void setLogger(Logger logger) {
        LoggingExecutionListener.logger = logger;
    }
}
