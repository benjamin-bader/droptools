package com.bendb.dropwizard.redis;

import com.bendb.dropwizard.redis.jersey.JedisPoolBinder;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.core.Configuration;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.JedisPool;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void setup() {
        bundle = new JedisBundle<TestConfig>() {
            @Override
            public JedisFactory getJedisFactory(TestConfig configuration) {
                return jedisFactory;
            }
        };

        lenient().when(environment.healthChecks()).thenReturn(healthChecks);
        lenient().when(environment.jersey()).thenReturn(jerseyEnvironment);
        lenient().when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
        lenient().when(environment.metrics()).thenReturn(metricRegistry);

        lenient().when(jedisFactory.build(environment)).thenReturn(pool);
        lenient().when(pool.getNumActive()).thenReturn(10);
        lenient().when(pool.getNumIdle()).thenReturn(5);
        lenient().when(pool.getNumWaiters()).thenReturn(1);
    }

    @Test
    public void bootstrapsNothing() throws Exception {
        bundle.initialize(bootstrap);
        verifyNoInteractions(bootstrap);
    }

    @Test
    public void createsJedisPoolFromFactory() throws Exception {
        bundle.run(config, environment);
        verify(jedisFactory).build(environment);
        verify(metricRegistry, times(3)).register(Mockito.anyString(), Mockito.any(Gauge.class));
        assertThat(bundle.getPool(), equalTo(pool));
    }

    @Test
    public void registersHealthCheck() throws Exception {
        bundle.run(config, environment);

        ArgumentCaptor<JedisHealthCheck> captor = ArgumentCaptor.forClass(JedisHealthCheck.class);
        verify(healthChecks).register(eq("redis"), captor.capture());

        assertThat(captor.getValue(), not(nullValue()));
    }

    @Test
    public void registersJedisInjectableProvider() throws Exception {
        bundle.run(config, environment);

        verify(jerseyEnvironment, atLeastOnce()).register(isA(JedisPoolBinder.class));
    }
}