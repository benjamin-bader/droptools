package com.bendb.dropwizard.jooq;

import com.google.common.base.Optional;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.jooq.tools.jdbc.JDBCUtils;

import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * A factory for jOOQ {@link org.jooq.Configuration} objects.
 * <p>
 * <strong>Configuration Parameters</strong>
 * <table summary="Descriptions of all JooqFactory parameters">
 *     <tr>
 *         <th>Name</th>
 *         <th>Default</th>
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td>dialect</td>
 *         <td>{@code null}</td>
 *         <td>
 *             Inferred from the JDBC url if absent.  If present, any name
 *             from the {@link org.jooq.SQLDialect} enumeration.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>logExecutedSql</td>
 *         <td>{@code false}</td>
 *         <td><strong>DEPRECATED:</strong> Use {@code executeLogging} instead.</td>
 *     </tr>
 *     <tr>
 *         <td>renderSchema</td>
 *         <td>{@code true}</td>
 *         <td>Whether any schema is rendered at all; disable for single-schema environments.</td>
 *     </tr>
 *     <tr>
 *         <td>renderNameStyle</td>
 *         <td>QUOTED</td>
 *         <td>
 *             Whether rendered schema, table, column, etc. names should be quoted in rendered SQL,
 *             or transformed in any way.  Valid values are QUOTED, UPPER, LOWER, and AS_IS.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>renderKeywordStyle</td>
 *         <td>UPPER</td>
 *         <td>Whether SQL keywords should be rendered in upper or lower case.</td>
 *     </tr>
 *     <tr>
 *         <td>renderFormatted</td>
 *         <td>{@code false}</td>
 *         <td>Whether generated SQL should be pretty-printed.</td>
 *     </tr>
 *     <tr>
 *         <td>paramType</td>
 *         <td>INDEXED</td>
 *         <td>
 *             How rendered bind values should be rendered.  Valid values are:
 *             <ul>
 *                 <li>INDEXED</li>
 *                 <li>NAMED</li>
 *                 <li>INLINED</li>
 *             </ul>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>statementType</td>
 *         <td>PREPARED_STATEMENT</td>
 *         <td>
 *             Whether to use prepared statements with bind values, or static SQL.
 *             Valid values are PREPARED_STATEMENT and STATIC_STATEMENT.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>executeLogging</td>
 *         <td>{@code false}</td>
 *         <td>
 *             Whether to enable logging of jOOQ's generated SQL queries, as
 *             well as other built-in logging.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>executeWithOptimisticLocking</td>
 *         <td>{@code false}</td>
 *         <td>
 *             Whether {@code store()} and {@code delete()} should be executed
 *             with optimistic locking.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>attachRecords</td>
 *         <td>{@code true}</td>
 *         <td>Whether fetched records should be attached to the fetching configuration.</td>
 *     </tr>
 *     <tr>
 *         <td>updatablePrimaryKeys</td>
 *         <td>{@code false}</td>
 *         <td>
 *             Whether primary-key values are deemed "updatable" in jOOQ.
 *             Setting this to {@code true} will allow for updating primary keys
 *             through {@link org.jooq.UpdatableRecord#store()} and {@link org.jooq.UpdatableRecord#update()}.
 *         </td>
 *     </tr>
 * </table>
 */
public class JooqFactory {
    private static final String DEFAULT_NAME = "jooq";

    @NotNull
    @UnwrapValidatedValue(false)
    private Optional<SQLDialect> dialect = Optional.absent();

    private boolean renderSchema = true;

    @NotNull
    private RenderNameStyle renderNameStyle = RenderNameStyle.QUOTED;

    @NotNull
    private RenderKeywordStyle renderKeywordStyle = RenderKeywordStyle.UPPER;

    private boolean renderFormatted = false;

    @NotNull
    private ParamType paramType = ParamType.INDEXED;

    @NotNull
    private StatementType statementType = StatementType.PREPARED_STATEMENT;

    private boolean executeLogging = false;

    private boolean executeWithOptimisticLocking = false;

    private boolean attachRecords = true;

    private boolean updatablePrimaryKeys = false;

    private boolean fetchWarnings = true;

    public Optional<SQLDialect> getDialect() {
        return dialect;
    }

    public void setDialect(Optional<SQLDialect> dialect) {
        this.dialect = dialect;
    }

    /**
     * Deprecated; this is now a synonym for {@link #isExecuteLogging()}.
     * @deprecated use {@link #isExecuteLogging()} instead.
     */
    @Deprecated
    public boolean isLogExecutedSql() {
        return executeLogging;
    }

    /**
     * Deprecated; this is now a synonym for {@link #setExecuteLogging(boolean)}.
     * @deprecated use {@link #setExecuteLogging(boolean)} instead.
     */
    @Deprecated
    public void setLogExecutedSql(boolean logExecutedSql) {
        this.executeLogging = logExecutedSql;
    }

    public boolean isRenderSchema() {
        return renderSchema;
    }

    public void setRenderSchema(boolean renderSchema) {
        this.renderSchema = renderSchema;
    }

    public RenderNameStyle getRenderNameStyle() {
        return renderNameStyle;
    }

    public void setRenderNameStyle(RenderNameStyle renderNameStyle) {
        this.renderNameStyle = renderNameStyle;
    }

    public RenderKeywordStyle getRenderKeywordStyle() {
        return renderKeywordStyle;
    }

    public void setRenderKeywordStyle(RenderKeywordStyle renderKeywordStyle) {
        this.renderKeywordStyle = renderKeywordStyle;
    }

    public boolean isRenderFormatted() {
        return renderFormatted;
    }

    public void setRenderFormatted(boolean renderFormatted) {
        this.renderFormatted = renderFormatted;
    }

    public ParamType getParamType() {
        return paramType;
    }

    public void setParamType(ParamType paramType) {
        this.paramType = paramType;
    }

    public StatementType getStatementType() {
        return statementType;
    }

    public void setStatementType(StatementType statementType) {
        this.statementType = statementType;
    }

    public boolean isExecuteLogging() {
        return executeLogging;
    }

    public void setExecuteLogging(boolean executeLogging) {
        this.executeLogging = executeLogging;
    }

    public boolean isExecuteWithOptimisticLocking() {
        return executeWithOptimisticLocking;
    }

    public void setExecuteWithOptimisticLocking(boolean executeWithOptimisticLocking) {
        this.executeWithOptimisticLocking = executeWithOptimisticLocking;
    }

    public boolean isAttachRecords() {
        return attachRecords;
    }

    public void setAttachRecords(boolean attachRecords) {
        this.attachRecords = attachRecords;
    }

    public boolean isUpdatablePrimaryKeys() {
        return updatablePrimaryKeys;
    }

    public void setUpdatablePrimaryKeys(boolean updatablePrimaryKeys) {
        this.updatablePrimaryKeys = updatablePrimaryKeys;
    }

    public boolean isFetchWarnings() {
        return fetchWarnings;
    }

    public void setFetchWarnings(boolean fetchWarnings) {
        this.fetchWarnings = fetchWarnings;
    }

    public Configuration build(Environment environment, DataSourceFactory factory) throws ClassNotFoundException {
        return build(environment, factory, DEFAULT_NAME);
    }

    public Configuration build(Environment environment, PooledDataSourceFactory factory, String name) throws ClassNotFoundException {
        final Settings settings = buildSettings();
        final ManagedDataSource dataSource = factory.build(environment.metrics(), name);
        final SQLDialect dialect = determineDialect(factory, dataSource);
        final ConnectionProvider connectionProvider = new DataSourceConnectionProvider(dataSource);
        final Configuration config = new DefaultConfiguration()
                .set(settings)
                .set(dialect)
                .set(connectionProvider);

        environment.lifecycle().manage(dataSource);

        return config;
    }

    private SQLDialect determineDialect(PooledDataSourceFactory dataSourceFactory, ManagedDataSource dataSource) {
        // If a dialect was specified, great!
        if (getDialect().isPresent()) {
            return dialect.get();
        }

        return JDBCUtils.dialect(dataSourceFactory.getUrl());
    }

    private Settings buildSettings() {
        final Settings settings = new Settings();
        settings.setRenderSchema(renderSchema);
        settings.setRenderNameStyle(renderNameStyle);
        settings.setRenderKeywordStyle(renderKeywordStyle);
        settings.setRenderFormatted(renderFormatted);
        settings.setParamType(paramType);
        settings.setStatementType(statementType);
        settings.setExecuteLogging(executeLogging);
        settings.setExecuteWithOptimisticLocking(executeWithOptimisticLocking);
        settings.setAttachRecords(attachRecords);
        settings.setUpdatablePrimaryKeys(updatablePrimaryKeys);
        settings.setFetchWarnings(fetchWarnings);

        return settings;
    }
}
