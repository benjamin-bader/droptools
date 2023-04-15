package com.bendb.dropwizard.jooq;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.core.setup.Environment;
import org.jooq.Configuration;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JooqFactoryTest {
    @Mock DataSourceFactory dataSourceFactory;
    @Mock ManagedDataSource managedDataSource;
    @Mock Environment environment;
    @Mock LifecycleEnvironment lifecycle;
    final static String DATASOURCE_NAME = "database";

    private JooqFactory factory;

    @Before
    public void setup() throws Exception {
        when(environment.lifecycle()).thenReturn(lifecycle);
        when(dataSourceFactory.build(any(MetricRegistry.class), anyString())).thenReturn(managedDataSource);

        factory = new JooqFactory();
        factory.setDialect(Optional.of(SQLDialect.H2));
    }

    @Test
    public void buildsConfigurationUsingDataSourceFactory() throws Exception {
        Configuration config = factory.build(environment, dataSourceFactory);
        DataSourceConnectionProvider provider = (DataSourceConnectionProvider) config.connectionProvider();
        assertThat(provider.dataSource()).isEqualTo(managedDataSource);
    }

    @Test
    public void buildsConfigurationUsingDataSourceFactoryAndName() throws Exception {
        Configuration config = factory.build(environment, dataSourceFactory, DATASOURCE_NAME);
        DataSourceConnectionProvider provider = (DataSourceConnectionProvider) config.connectionProvider();
        assertThat(provider.dataSource()).isEqualTo(managedDataSource);
    }

    @Test
    public void infersDialectFromJdbcUrlWhenDialectIsNotSpecified() throws Exception {
        when(dataSourceFactory.getUrl()).thenReturn("jdbc:postgresql://localhost:5432/test");

        factory.setDialect(Optional.<SQLDialect>absent());
        Configuration config = factory.build(environment, dataSourceFactory);
        assertThat(config.dialect()).isEqualTo(SQLDialect.POSTGRES);
    }

    @Test
    public void usesSpecifiedDialect() throws Exception {
        when(dataSourceFactory.getUrl()).thenReturn("jdbc:postgresql://localhost:5432/test");

        factory.setDialect(Optional.of(SQLDialect.DERBY));
        Configuration config = factory.build(environment, dataSourceFactory);
        assertThat(config.dialect()).isEqualTo(SQLDialect.DERBY);
    }

    @Test
    public void managesManagedDataSource() throws Exception {
        factory.build(environment, dataSourceFactory);
        verify(lifecycle).manage(managedDataSource);
    }

    @Test
    public void logExecutedSqlIsASynonymOfExecuteLogging() throws Exception {
        factory.setLogExecutedSql(true);
        assertThat(factory.isLogExecutedSql()).isTrue();
        assertThat(factory.isExecuteLogging()).isTrue();

        factory.setLogExecutedSql(false);
        assertThat(factory.isLogExecutedSql()).isFalse();
        assertThat(factory.isExecuteLogging()).isFalse();

        factory.setExecuteLogging(true);
        assertThat(factory.isLogExecutedSql()).isTrue();
        assertThat(factory.isExecuteLogging()).isTrue();

        factory.setExecuteLogging(false);
        assertThat(factory.isLogExecutedSql()).isFalse();
        assertThat(factory.isExecuteLogging()).isFalse();
    }
}