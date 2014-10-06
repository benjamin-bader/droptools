package com.bendb.dropwizard.jooq;

import com.bendb.dropwizard.jooq.jersey.DSLContextInjectableProvider;
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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JooqBundleTest {
    @Mock DataSourceFactory dataSourceFactory;
    @Mock JooqFactory jooqFactory;
    @Mock Configuration configuration;
    @Mock Environment environment;
    @Mock Bootstrap<DropwizardConfig> bootstrap;
    @Mock JerseyEnvironment jerseyEnvironment;
    @Mock HealthCheckRegistry healthChecks;

    private String validationQuery = "this is a query";
    private JooqBundle<DropwizardConfig> jooqBundle = new JooqBundle<DropwizardConfig>() {
        @Override
        public JooqFactory getJooqFactory(DropwizardConfig configuration) {
            return jooqFactory;
        }

        @Override
        public DataSourceFactory getDataSourceFactory(DropwizardConfig configuration) {
            return dataSourceFactory;
        }
    };

    @Before
    public void setup() throws Exception {
        when(environment.jersey()).thenReturn(jerseyEnvironment);
        when(environment.healthChecks()).thenReturn(healthChecks);
        when(jooqFactory.build(environment, dataSourceFactory)).thenReturn(configuration);
        when(dataSourceFactory.getValidationQuery()).thenReturn(validationQuery);
    }

    @Test
    public void buildsAJooqConfiguration() throws Exception {
        jooqBundle.run(new DropwizardConfig(), environment);
        verify(jooqFactory).build(environment, dataSourceFactory);
    }

    @Test
    public void registersAnInjectibleProvider() throws Exception {
        jooqBundle.run(new DropwizardConfig(), environment);

        ArgumentCaptor<DSLContextInjectableProvider> captor = ArgumentCaptor.forClass(DSLContextInjectableProvider.class);
        verify(jerseyEnvironment, atLeastOnce()).register(captor.capture());
    }

    @Test
    public void registersAHealthCheck() throws Exception {
        jooqBundle.run(new DropwizardConfig(), environment);

        ArgumentCaptor<JooqHealthCheck> captor = ArgumentCaptor.forClass(JooqHealthCheck.class);
        verify(healthChecks).register(eq("jooq"), captor.capture());

        assertThat(captor.getValue().getValidationQuery()).isEqualTo(validationQuery);
    }

    @Test
    public void providesADefaultJooqFactory() throws Exception {
        JooqFactory defaultFactory = new JooqFactory();
        JooqFactory jooqFactory = new JooqBundle<DropwizardConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(DropwizardConfig configuration) {
                return dataSourceFactory;
            }
        }.getJooqFactory(new DropwizardConfig());

        assertThat(jooqFactory).isNotNull();
    }

    @Test
    public void providesBuiltJooqConfiguration() throws Exception {
        assertThat(jooqBundle.getConfiguration()).isNull();
        jooqBundle.run(new DropwizardConfig(), environment);
        assertThat(jooqBundle.getConfiguration()).is(configuration);
    }

    private static final class DropwizardConfig extends io.dropwizard.Configuration {}
}