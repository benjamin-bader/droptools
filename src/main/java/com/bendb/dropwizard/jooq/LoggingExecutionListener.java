package com.bendb.dropwizard.jooq;

import com.google.common.base.Strings;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultExecuteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExecutionListener extends DefaultExecuteListener {
    private static final Logger LOGGER = LoggerFactory.getLogger("jooq.execution");

    private final DSLContext create;

    public LoggingExecutionListener(SQLDialect dialect, Settings settings) {
        Settings s = (Settings) settings.clone();
        s.setRenderFormatted(true);

        this.create = DSL.using(dialect, s);
    }

    @Override
    public void executeStart(ExecuteContext ctx) {
        super.executeStart(ctx);

        String statement = null;
        if (ctx.query() != null) {
            statement = create.renderInlined(ctx.query());
        } else if (ctx.routine() != null) {
            statement = create.renderInlined(ctx.routine());
        } else if (!Strings.isNullOrEmpty(ctx.sql())) {
            statement = ctx.sql();
        }

        if (statement != null) {
            LOGGER.info(statement);
        }
    }
}
