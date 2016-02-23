package com.bendb.dropwizard.redis;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.Configuration;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import redis.clients.jedis.JedisPool;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JedisBundleTest {
    static class TestConfig extends Configuration {}

    @Mock Bootstrap<?> bootstrap;
    @Mock Environment environment;
    @Mock JerseyEnvironment jerseyEnvironment;
    @Mock LifecycleEnvironment lifecycleEnvironment;
    @Mock MetricRegistry metricRegistry;
    @Mock HealthCheckRegistry healthChecks;
    @Mock TestConfig config;
    @Mock JedisFactory jedisFactory;
    @Mock JedisPool pool;

    private JedisBundle<TestConfig> bundle;

    @Before
    public void setup() {
        bundle = new JedisBundle<TestConfig>() {
            @Override
            public JedisFactory getJedisFactory(TestConfig configuration) {
                return jedisFactory;
            }
        };

        when(environment.healthChecks()).thenReturn(healthChecks);
        when(environment.jersey()).thenReturn(jerseyEnvironment);
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
        when(environment.metrics()).thenReturn(metricRegistry);

        when(jedisFactory.build(environment)).thenReturn(pool);
        when(pool.getNumActive()).thenReturn(10);
        when(pool.getNumIdle()).thenReturn(5);
        when(pool.getNumWaiters()).thenReturn(1);
    }

    @Test
    public void bootstrapsNothing() throws Exception {
        bundle.initialize(bootstrap);
        verifyZeroInteractions(bootstrap);
    }

    @Test
    public void createsJedisPoolFromFactory() throws Exception {
        bundle.run(config, environment);
        verify(jedisFactory).build(environment);
        verify(metricRegistry, times(3)).register(Mockito.anyString(), Mockito.any(Gauge.class));
        assertThat(bundle.getPool()).is(pool);
    }

    @Test
    public void registersHealthCheck() throws Exception {
        bundle.run(config, environment);

        ArgumentCaptor<JedisHealthCheck> captor = ArgumentCaptor.forClass(JedisHealthCheck.class);
        verify(healthChecks).register(eq("redis"), captor.capture());

        assertThat(captor.getValue()).isNotNull();
    }

    @Test
    public void registersJedisInjectableProvider() throws Exception {
        bundle.run(config, environment);

        ArgumentCaptor<JedisFactory> captor = ArgumentCaptor.forClass(JedisFactory.class);
        verify(jerseyEnvironment, atLeastOnce()).register(captor.capture());

        assertThat(captor.getValue()).isNotNull();
    }
}