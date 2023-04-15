package com.bendb.dropwizard.redis;

import com.google.common.net.HostAndPort;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.core.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;

import static com.bendb.dropwizard.redis.testing.Subjects.assertThat;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JedisFactoryTest {
    @Mock Environment environment;
    @Mock JerseyEnvironment jerseyEnvironment;
    @Mock LifecycleEnvironment lifecycleEnvironment;

    JedisFactory factory;

    @Before
    public void setup() {
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
        factory = new JedisFactory();
    }

    @Test
    public void setsEndpointFromUrl() throws Exception {
        factory.setUrl(new URI("redis://foohost:1234"));

        assertThat(factory).hasHost("foohost");
        assertThat(factory).hasPort(1234);
    }

    @Test
    public void setsPasswordFromUrl() throws Exception {
        factory.setUrl(new URI("redis://u:swordfish@foohost:1234"));

        assertThat(factory).hasPassword("swordfish");
    }

    @Test
    public void setsSslFromUrl() throws Exception {
        factory.setUrl(new URI("rediss://u:swordfish@foohost:1234"));

        assertThat(factory).hasSsl(true);
    }

    @Test
    public void setsSslFromConfiguration() throws Exception {
        factory.setSsl(true);

        assertThat(factory).hasSsl(true);
    }

    @Test
    public void setsSslFromConfigurationAndIgnoresSchemeFromUrl() throws Exception {
        factory.setSsl(true);
        factory.setUrl(new URI("redis://u:swordfish@foohost:1234"));

        assertThat(factory).hasSsl(true);
    }

    @Test
    public void assumesDefaultPortIfNoneGiven() {
        factory.setEndpoint(HostAndPort.fromString("localhost"));

        assertThat(factory).hasDefaultRedisPort();
    }

    @Test
    public void checkPasswordIfSet() {
        factory.setPassword(null);
        assertThat(factory).hasNullPassword();
        factory.setPassword("swordfish");
        assertThat(factory).hasPassword("swordfish");
    }

    @Test
    public void getsHostAndPortFromEndpoint() {
        factory.setEndpoint(HostAndPort.fromString("127.0.0.2:11211"));

        assertThat(factory).hasHost("127.0.0.2");
        assertThat(factory).hasPort(11211);
    }

    @Test
    public void managesCreatedJedisPool() {
        factory.setEndpoint(HostAndPort.fromString("localhost"));
        factory.build(environment);

        ArgumentCaptor<JedisPoolManager> managerCaptor = ArgumentCaptor.forClass(JedisPoolManager.class);
        verify(lifecycleEnvironment).manage(managerCaptor.capture());

        assertThat(managerCaptor.getValue()).isNotNull();
    }

    @Test
    public void setsDefaultIdleAndTotalConnections() {
        assertThat(factory).hasDefaultMinIdleConnections();
        assertThat(factory).hasDefaultMaxIdleConnections();
        assertThat(factory).hasDefaultTotalConnections();
    }
}
