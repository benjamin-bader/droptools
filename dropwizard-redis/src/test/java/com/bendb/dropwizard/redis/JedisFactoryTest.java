package com.bendb.dropwizard.redis;

import com.google.common.net.HostAndPort;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.core.setup.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.net.URI;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class JedisFactoryTest {
    @Mock Environment environment;
    @Mock JerseyEnvironment jerseyEnvironment;
    @Mock LifecycleEnvironment lifecycleEnvironment;

    JedisFactory factory;

    @BeforeEach
    public void setup() {
        lenient().when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
        factory = new JedisFactory();
    }

    @Test
    public void setsEndpointFromUrl() throws Exception {
        factory.setUrl(new URI("redis://foohost:1234"));

        assertThat(factory.getHost(), equalTo("foohost"));
        assertThat(factory.getPort(), equalTo(1234));
    }

    @Test
    public void setsPasswordFromUrl() throws Exception {
        factory.setUrl(new URI("redis://u:swordfish@foohost:1234"));

        assertThat(factory.getPassword(), equalTo("swordfish"));
    }

    @Test
    public void setsSslFromUrl() throws Exception {
        factory.setUrl(new URI("rediss://u:swordfish@foohost:1234"));

        assertThat(factory.getSsl(), is(true));
    }

    @Test
    public void setsSslFromConfiguration() throws Exception {
        factory.setSsl(true);

        assertThat(factory.getSsl(), is(true));
    }

    @Test
    public void setsSslFromConfigurationAndIgnoresSchemeFromUrl() throws Exception {
        factory.setSsl(true);
        factory.setUrl(new URI("redis://u:swordfish@foohost:1234"));

        assertThat(factory.getSsl(), is(true));
    }

    @Test
    public void assumesDefaultPortIfNoneGiven() {
        factory.setEndpoint(HostAndPort.fromString("localhost"));

        assertThat(factory.getPort(), equalTo(6379));
    }

    @Test
    public void checkPasswordIfSet() {
        factory.setPassword(null);
        assertThat(factory.getPassword(), nullValue());
        factory.setPassword("swordfish");
        assertThat(factory.getPassword(), equalTo("swordfish"));
    }

    @Test
    public void getsHostAndPortFromEndpoint() {
        factory.setEndpoint(HostAndPort.fromString("127.0.0.2:11211"));

        assertThat(factory.getHost(), equalTo("127.0.0.2"));
        assertThat(factory.getPort(), equalTo(11211));
    }

    @Test
    public void managesCreatedJedisPool() {
        factory.setEndpoint(HostAndPort.fromString("localhost"));
        factory.build(environment);

        ArgumentCaptor<JedisPoolManager> managerCaptor = ArgumentCaptor.forClass(JedisPoolManager.class);
        verify(lifecycleEnvironment).manage(managerCaptor.capture());

        assertThat(managerCaptor.getValue(), not(nullValue()));
    }

    @Test
    public void setsDefaultIdleAndTotalConnections() {
        assertThat(factory.getMinIdle(), equalTo(0));
        assertThat(factory.getMaxIdle(), equalTo(0));
        assertThat(factory.getMaxTotal(), equalTo(1024));
    }
}
