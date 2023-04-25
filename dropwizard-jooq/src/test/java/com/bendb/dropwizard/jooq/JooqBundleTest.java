package com.bendb.dropwizard.jooq;

import com.bendb.dropwizard.jooq.jersey.DSLContextFeature;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import org.jooq.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    static final String validationQueryPrimary = "this is a query for primary";
    static final String validationQueryReplica = "this is a query for replica";

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

    @BeforeEach
    public void setup() throws Exception {
        lenient().when(environment.jersey()).thenReturn(jerseyEnvironment);
        lenient().when(environment.healthChecks()).thenReturn(healthChecks);
        lenient().when(jooqFactory.build(environment, dataSourceFactoryPrimary, DEFAULT_NAME)).thenReturn(jooqConfigPrimary);
        lenient().when(jooqFactory.build(environment, dataSourceFactoryPrimary, DATASOURCE_PRIMARY)).thenReturn(jooqConfigPrimary);
        lenient().when(jooqFactory.build(environment, dataSourceFactoryReplica, DATASOURCE_REPLICA)).thenReturn(jooqConfigReplica);
        lenient().when(dataSourceFactoryPrimary.getValidationQuery()).thenReturn(Optional.of(validationQueryPrimary));
        lenient().when(dataSourceFactoryReplica.getValidationQuery()).thenReturn(Optional.of(validationQueryReplica));
    }

    @Test
    public void buildsAJooqConfiguration() throws Exception {
        jooqBundle.run(new DropwizardConfig(), environment);
        verify(jooqFactory).build(environment, dataSourceFactoryPrimary, DEFAULT_NAME);
        Assertions.assertEquals(jooqBundle.getConfiguration(), jooqConfigPrimary);
    }

    @Test
    public void doesNothingInBootstrap() {
        jooqBundle.initialize(bootstrap);
        verifyNoInteractions(bootstrap);
    }

    @Test
    public void registersAnInjectibleProviderOnParameters() throws Exception {
        jooqBundle.run(new DropwizardConfig(), environment);

        verify(jerseyEnvironment, atLeastOnce()).register(isA(DSLContextFeature.class));
    }

    @Test
    public void registersAHealthCheck() throws Exception {
        jooqBundle.run(new DropwizardConfig(), environment);

        ArgumentCaptor<JooqHealthCheck> captor = ArgumentCaptor.forClass(JooqHealthCheck.class);
        verify(healthChecks).register(eq(DEFAULT_NAME), captor.capture());

        Assertions.assertEquals(captor.getValue().getValidationQuery(), validationQueryPrimary);
    }

    @Test
    public void registersMultipleHealthChecks() throws Exception {
        jooqBundleMultiDS.run(new DropwizardConfig(), environment);

        ArgumentCaptor<JooqHealthCheck> captor = ArgumentCaptor.forClass(JooqHealthCheck.class);

        verify(healthChecks).register(eq(DATASOURCE_PRIMARY), captor.capture());
        Assertions.assertEquals(captor.getValue().getValidationQuery(), validationQueryPrimary);

        verify(healthChecks).register(eq(DATASOURCE_REPLICA), captor.capture());
        Assertions.assertEquals(captor.getValue().getValidationQuery(), validationQueryReplica);
    }

    @Test
    public void providesADefaultJooqFactory() throws Exception {
        JooqFactory jooqFactory = new JooqBundle<DropwizardConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(DropwizardConfig configuration) {
                return dataSourceFactoryPrimary;
            }
        }.getJooqFactory(new DropwizardConfig());

        Assertions.assertNotNull(jooqFactory);
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
        assertThat(jooqBundle.getConfigurationMap(), hasKey(DEFAULT_NAME));
    }

    @Test
    public void providesBuiltJooqConfiguration() throws Exception {
        assertThat(jooqBundle.getConfiguration(), nullValue());
        jooqBundle.run(new DropwizardConfig(), environment);
        assertThat(jooqBundle.getConfiguration(), equalTo(jooqConfigPrimary));
    }

    @Test
    public void providesMultipleJooqConfigurations() throws Exception {
        assertThat(jooqBundleMultiDS.getConfigurationMap(), is(anEmptyMap()));
        jooqBundleMultiDS.run(new DropwizardConfig(), environment);
        assertThat(jooqBundleMultiDS.getConfigurationMap(), hasKey(DATASOURCE_PRIMARY));
        assertThat(jooqBundleMultiDS.getConfigurationMap(), hasKey(DATASOURCE_REPLICA));
    }

    private static final class DropwizardConfig extends io.dropwizard.core.Configuration {}
}