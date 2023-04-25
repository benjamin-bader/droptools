package com.bendb.dropwizard.jooq;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.util.Optional;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JooqFactoryTest {
    @Mock PooledDataSourceFactory dataSourceFactory;
    @Mock ManagedDataSource managedDataSource;
    @Mock Environment environment;
    @Mock LifecycleEnvironment lifecycle;
    @Mock MetricRegistry metricRegistry;
    final static String DATASOURCE_NAME = "database";

    private JooqFactory factory;

    @BeforeEach
    public void setup() {
        lenient().when(environment.lifecycle()).thenReturn(lifecycle);
        lenient().when(environment.metrics()).thenReturn(metricRegistry);
        lenient().when(dataSourceFactory.build(any(MetricRegistry.class), anyString())).thenReturn(managedDataSource);

        factory = new JooqFactory();
        factory.setDialect(Optional.of(SQLDialect.H2));
    }

    @Test
    public void buildsConfigurationUsingDataSourceFactory() throws Exception {
        Configuration config = factory.build(environment, dataSourceFactory);
        DataSourceConnectionProvider provider = (DataSourceConnectionProvider) config.connectionProvider();
        assertThat(provider.dataSource(), equalTo(managedDataSource));
    }

    @Test
    public void buildsConfigurationUsingDataSourceFactoryAndName() throws Exception {
        Configuration config = factory.build(environment, dataSourceFactory, DATASOURCE_NAME);
        DataSourceConnectionProvider provider = (DataSourceConnectionProvider) config.connectionProvider();
        assertThat(provider.dataSource(), equalTo(managedDataSource));
    }

    @Test
    public void infersDialectFromJdbcUrlWhenDialectIsNotSpecified() throws Exception {
        doReturn("jdbc:postgresql://localhost:5432/test").when(dataSourceFactory).getUrl();

        factory.setDialect(Optional.empty());
        Configuration config = factory.build(environment, dataSourceFactory);
        assertThat(config.dialect(), equalTo(SQLDialect.POSTGRES));
    }

    @Test
    public void usesSpecifiedDialect() throws Exception {
        lenient().doReturn("jdbc:postgresql://localhost:5432/test").when(dataSourceFactory).getUrl();

        factory.setDialect(Optional.of(SQLDialect.DERBY));
        Configuration config = factory.build(environment, dataSourceFactory);
        assertThat(config.dialect(), equalTo(SQLDialect.DERBY));
    }

    @Test
    public void managesManagedDataSource() throws Exception {
        factory.build(environment, dataSourceFactory);
        verify(lifecycle).manage(managedDataSource);
    }

    @Test
    public void logExecutedSqlIsASynonymOfExecuteLogging() throws Exception {
        factory.setLogExecutedSql(true);
        assertThat(factory.isLogExecutedSql(), is(true));
        assertThat(factory.isExecuteLogging(), is(true));

        factory.setLogExecutedSql(false);
        assertThat(factory.isLogExecutedSql(), is(false));
        assertThat(factory.isExecuteLogging(), is(false));

        factory.setExecuteLogging(true);
        assertThat(factory.isLogExecutedSql(), is(true));
        assertThat(factory.isExecuteLogging(), is(true));

        factory.setExecuteLogging(false);
        assertThat(factory.isLogExecutedSql(), is(false));
        assertThat(factory.isExecuteLogging(), is(false));
    }
}