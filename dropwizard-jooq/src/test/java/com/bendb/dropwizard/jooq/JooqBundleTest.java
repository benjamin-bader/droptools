package com.bendb.dropwizard.jooq;

import com.bendb.dropwizard.jooq.jersey.JooqBinder;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jooq.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.SortedMap;
import java.util.TreeMap;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JooqBundleTest {
    @Mock DataSourceFactory dataSourceFactoryPrimary;
    @Mock DataSourceFactory dataSourceFactoryReplica;
    @Mock JooqFactory jooqFactory;
    @Mock Configuration jooqConfigPrimary;
    @Mock Configuration jooqConfigReplica;
    @Mock Environment environment;
    @Mock Bootstrap<DropwizardConfig> bootstrap;
    @Mock JerseyEnvironment jerseyEnvironment;
    @Mock HealthCheckRegistry healthChecks;
    final static String DEFAULT_NAME = "jooq";
    final static String DATASOURCE_PRIMARY = "primary";
    final static String DATASOURCE_REPLICA = "replica";

    private String validationQueryPrimary = "this is a query for primary";
    private String validationQueryReplica = "this is a query for replica";

    private JooqBundle<DropwizardConfig> jooqBundle = new JooqBundle<DropwizardConfig>() {
        @Override
        public JooqFactory getJooqFactory(DropwizardConfig configuration) {
            return jooqFactory;
        }

        @Override
        public DataSourceFactory getDataSourceFactory(DropwizardConfig configuration) {
            return dataSourceFactoryPrimary;
        }
    };

    private JooqBundle<DropwizardConfig> jooqBundleMultiDS = new JooqBundle<DropwizardConfig>() {
        @Override
        public JooqFactory getJooqFactory(DropwizardConfig configuration) {
            return jooqFactory;
        }

        @Override
        public DataSourceFactory getDataSourceFactory(DropwizardConfig configuration) {
            return dataSourceFactoryPrimary;
        }

        @Override
        public SortedMap<String,DataSourceFactory> getSecondaryDataSourceFactories(DropwizardConfig configuration) {
            final SortedMap<String,DataSourceFactory> dataSourceFactoryMap = new TreeMap<>();
            dataSourceFactoryMap.put(DATASOURCE_REPLICA, dataSourceFactoryReplica);
            return dataSourceFactoryMap;
        }

        @Override
        public String primaryDataSourceName() {
            return DATASOURCE_PRIMARY;
        }
    };

    @Before
    public void setup() throws Exception {
        when(environment.jersey()).thenReturn(jerseyEnvironment);
        when(environment.healthChecks()).thenReturn(healthChecks);
        when(jooqFactory.build(environment, dataSourceFactoryPrimary, DEFAULT_NAME)).thenReturn(jooqConfigPrimary);
        when(jooqFactory.build(environment, dataSourceFactoryPrimary, DATASOURCE_PRIMARY)).thenReturn(jooqConfigPrimary);
        when(jooqFactory.build(environment, dataSourceFactoryReplica, DATASOURCE_REPLICA)).thenReturn(jooqConfigReplica);
        when(dataSourceFactoryPrimary.getValidationQuery()).thenReturn(validationQueryPrimary);
        when(dataSourceFactoryReplica.getValidationQuery()).thenReturn(validationQueryReplica);
    }

    @Test
    public void buildsAJooqConfiguration() throws Exception {
        jooqBundle.run(new DropwizardConfig(), environment);
        verify(jooqFactory).build(environment, dataSourceFactoryPrimary, DEFAULT_NAME);
        assertThat(jooqBundle.getConfiguration()).isEqualTo(jooqConfigPrimary);
    }

    @Test
    public void doesNothingInBootstrap() {
        jooqBundle.initialize(bootstrap);
        verifyZeroInteractions(bootstrap);
    }

    @Test
    public void registersAnInjectibleProviderOnParameters() throws Exception {
        jooqBundle.run(new DropwizardConfig(), environment);

        ArgumentCaptor<JooqBinder> captor = ArgumentCaptor.forClass(JooqBinder.class);
        verify(jerseyEnvironment, atLeastOnce()).register(captor.capture());
    }

    @Test
    public void registersAHealthCheck() throws Exception {
        jooqBundle.run(new DropwizardConfig(), environment);

        ArgumentCaptor<JooqHealthCheck> captor = ArgumentCaptor.forClass(JooqHealthCheck.class);
        verify(healthChecks).register(eq(DEFAULT_NAME), captor.capture());

        assertThat(captor.getValue().getValidationQuery()).isEqualTo(validationQueryPrimary);
    }

    @Test
    public void registersMultipleHealthChecks() throws Exception {
        jooqBundleMultiDS.run(new DropwizardConfig(), environment);

        ArgumentCaptor<JooqHealthCheck> captor = ArgumentCaptor.forClass(JooqHealthCheck.class);

        verify(healthChecks).register(eq(DATASOURCE_PRIMARY), captor.capture());
        assertThat(captor.getValue().getValidationQuery()).isEqualTo(validationQueryPrimary);

        verify(healthChecks).register(eq(DATASOURCE_REPLICA), captor.capture());
        assertThat(captor.getValue().getValidationQuery()).isEqualTo(validationQueryReplica);
    }

    @Test
    public void providesADefaultJooqFactory() throws Exception {
        JooqFactory jooqFactory = new JooqBundle<DropwizardConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(DropwizardConfig configuration) {
                return dataSourceFactoryPrimary;
            }
        }.getJooqFactory(new DropwizardConfig());

        assertThat(jooqFactory).isNotNull();
    }

    @Test
    public void providesADefaultJooqFactoryName() throws Exception {
        jooqBundle = new JooqBundle<DropwizardConfig>() {
            @Override
            public JooqFactory getJooqFactory(DropwizardConfig configuration) {
                return jooqFactory;
            }

            @Override
            public DataSourceFactory getDataSourceFactory(DropwizardConfig configuration) {
                return dataSourceFactoryPrimary;
            }
        };
        jooqBundle.run(new DropwizardConfig(), environment);
        assertThat(jooqBundle.getConfigurationMap().containsKey(DEFAULT_NAME));
    }

    @Test
    public void providesBuiltJooqConfiguration() throws Exception {
        assertThat(jooqBundle.getConfiguration()).isNull();
        jooqBundle.run(new DropwizardConfig(), environment);
        assertThat(jooqBundle.getConfiguration()).isEqualTo(jooqConfigPrimary);
    }

    @Test
    public void providesMultipleJooqConfigurations() throws Exception {
        assertThat(jooqBundleMultiDS.getConfigurationMap()).isEmpty();
        jooqBundleMultiDS.run(new DropwizardConfig(), environment);
        assertThat(jooqBundleMultiDS.getConfigurationMap().containsKey(DATASOURCE_PRIMARY));
        assertThat(jooqBundleMultiDS.getConfigurationMap().containsKey(DATASOURCE_REPLICA));
    }

    private static final class DropwizardConfig extends io.dropwizard.Configuration {}
}