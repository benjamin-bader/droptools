package com.bendb.dropwizard.redis;

import com.google.common.net.HostAndPort;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;

import static com.bendb.dropwizard.redis.testing.Subjects.jedisFactory;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assert_;
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

        assert_().about(jedisFactory()).that(factory).hasHost("foohost");
        assert_().about(jedisFactory()).that(factory).hasPort(1234);
    }

    @Test
    public void setsPasswordFromUrl() throws Exception {
        factory.setUrl(new URI("redis://u:swordfish@foohost:1234"));

        assert_().about(jedisFactory()).that(factory).hasPassword("swordfish");
    }

    @Test
    public void setsSslFromUrl() throws Exception {
        factory.setUrl(new URI("rediss://u:swordfish@foohost:1234"));

        assert_().about(jedisFactory()).that(factory).hasSsl(true);
    }

    @Test
    public void setsSslFromConfiguration() throws Exception {
        factory.setSsl(true);

        assert_().about(jedisFactory()).that(factory).hasSsl(true);
    }

    @Test
    public void setsSslFromConfigurationAndIgnoresSchemeFromUrl() throws Exception {
        factory.setSsl(true);
        factory.setUrl(new URI("redis://u:swordfish@foohost:1234"));

        assert_().about(jedisFactory()).that(factory).hasSsl(true);
    }

    @Test
    public void assumesDefaultPortIfNoneGiven() {
        factory.setEndpoint(HostAndPort.fromString("localhost"));

        assert_().about(jedisFactory()).that(factory).hasDefaultRedisPort();
    }

    @Test
    public void checkPasswordIfSet() {
        factory.setPassword(null);
        assert_().about(jedisFactory()).that(factory).hasNullPassword();
        factory.setPassword("swordfish");
        assert_().about(jedisFactory()).that(factory).hasPassword("swordfish");
    }

    @Test
    public void getsHostAndPortFromEndpoint() {
        factory.setEndpoint(HostAndPort.fromString("127.0.0.2:11211"));

        assert_().about(jedisFactory()).that(factory).hasHost("127.0.0.2");
        assert_().about(jedisFactory()).that(factory).hasPort(11211);
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
        assert_().about(jedisFactory()).that(factory).hasDefaultMinIdleConnections();
        assert_().about(jedisFactory()).that(factory).hasDefaultMaxIdleConnections();
        assert_().about(jedisFactory()).that(factory).hasDefaultTotalConnections();
    }
}
