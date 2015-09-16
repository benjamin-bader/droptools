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
    @Mock DataSourceFactory dataSourceFactoryMaster;
    @Mock DataSourceFactory dataSourceFactorySlave;
    @Mock JooqFactory jooqFactory;
    @Mock Configuration jooqConfigMaster;
    @Mock Configuration jooqConfigSlave;
    @Mock Environment environment;
    @Mock Bootstrap<DropwizardConfig> bootstrap;
    @Mock JerseyEnvironment jerseyEnvironment;
    @Mock HealthCheckRegistry healthChecks;
    final static String DEFAULT_NAME = "jooq";
    final static String DATASOURCE_MASTER = "master";
    final static String DATASOURCE_SLAVE = "slave";

    private String validationQueryMaster = "this is a query for master";
    private String validationQuerySlave= "this is a query for slave";

    private JooqBundle<DropwizardConfig> jooqBundle = new JooqBundle<DropwizardConfig>() {
        @Override
        public JooqFactory getJooqFactory(DropwizardConfig configuration) {
            return jooqFactory;
        }

        @Override
        public DataSourceFactory getDataSourceFactory(DropwizardConfig configuration) {
            return dataSourceFactoryMaster;
        }
    };

    private JooqBundle<DropwizardConfig> jooqBundleMultiDS = new JooqBundle<DropwizardConfig>() {
        @Override
        public JooqFactory getJooqFactory(DropwizardConfig configuration) {
            return jooqFactory;
        }

        @Override
        public DataSourceFactory getDataSourceFactory(DropwizardConfig configuration) {
            return dataSourceFactoryMaster;
        }

        @Override
        public SortedMap<String,DataSourceFactory> getSecondaryDataSourceFactories(DropwizardConfig configuration) {
            final SortedMap<String,DataSourceFactory> dataSourceFactoryMap = new TreeMap<>();
            dataSourceFactoryMap.put(DATASOURCE_SLAVE, dataSourceFactorySlave);
            return dataSourceFactoryMap;
        }

        @Override
        public String primaryDataSourceName() {
            return DATASOURCE_MASTER;
        }
    };

    @Before
    public void setup() throws Exception {
        when(environment.jersey()).thenReturn(jerseyEnvironment);
        when(environment.healthChecks()).thenReturn(healthChecks);
        when(jooqFactory.build(environment, dataSourceFactoryMaster, DEFAULT_NAME)).thenReturn(jooqConfigMaster);
        when(jooqFactory.build(environment, dataSourceFactoryMaster, DATASOURCE_MASTER)).thenReturn(jooqConfigMaster);
        when(jooqFactory.build(environment, dataSourceFactorySlave, DATASOURCE_SLAVE)).thenReturn(jooqConfigSlave);
        when(dataSourceFactoryMaster.getValidationQuery()).thenReturn(validationQueryMaster);
        when(dataSourceFactorySlave.getValidationQuery()).thenReturn(validationQuerySlave);
    }

    @Test
    public void buildsAJooqConfiguration() throws Exception {
        jooqBundle.run(new DropwizardConfig(), environment);
        verify(jooqFactory).build(environment, dataSourceFactoryMaster, DEFAULT_NAME);
        assertThat(jooqBundle.getConfiguration()).isEqualTo(jooqConfigMaster);
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

        assertThat(captor.getValue().getValidationQuery()).isEqualTo(validationQueryMaster);
    }

    @Test
    public void registersMultipleHealthChecks() throws Exception {
        jooqBundleMultiDS.run(new DropwizardConfig(), environment);

        ArgumentCaptor<JooqHealthCheck> captor = ArgumentCaptor.forClass(JooqHealthCheck.class);

        verify(healthChecks).register(eq(DATASOURCE_MASTER), captor.capture());
        assertThat(captor.getValue().getValidationQuery()).isEqualTo(validationQueryMaster);

        verify(healthChecks).register(eq(DATASOURCE_SLAVE), captor.capture());
        assertThat(captor.getValue().getValidationQuery()).isEqualTo(validationQuerySlave);
    }

    @Test
    public void providesADefaultJooqFactory() throws Exception {
        JooqFactory jooqFactory = new JooqBundle<DropwizardConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(DropwizardConfig configuration) {
                return dataSourceFactoryMaster;
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
                return dataSourceFactoryMaster;
            }
        };
        jooqBundle.run(new DropwizardConfig(), environment);
        assertThat(jooqBundle.getConfigurationMap().containsKey(DEFAULT_NAME));
    }

    @Test
    public void providesBuiltJooqConfiguration() throws Exception {
        assertThat(jooqBundle.getConfiguration()).isNull();
        jooqBundle.run(new DropwizardConfig(), environment);
        assertThat(jooqBundle.getConfiguration()).is(jooqConfigMaster);
    }

    @Test
    public void providesMultipleJooqConfigurations() throws Exception {
        assertThat(jooqBundleMultiDS.getConfigurationMap()).isEmpty();
        jooqBundleMultiDS.run(new DropwizardConfig(), environment);
        assertThat(jooqBundleMultiDS.getConfigurationMap().containsKey(DATASOURCE_MASTER));
        assertThat(jooqBundleMultiDS.getConfigurationMap().containsKey(DATASOURCE_SLAVE));
    }

    private static final class DropwizardConfig extends io.dropwizard.Configuration {}
}