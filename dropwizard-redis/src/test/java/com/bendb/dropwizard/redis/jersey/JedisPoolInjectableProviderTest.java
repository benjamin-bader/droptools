package com.bendb.dropwizard.redis.jersey;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.ws.rs.core.Context;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class JedisPoolInjectableProviderTest {
    @Mock ComponentContext ic;
    @Mock Context context;
    @Mock Parameter param;
    @Mock JedisPool pool;

    private JedisPoolInjectableProvider provider;

    @Before
    public void setup() {
        provider = new JedisPoolInjectableProvider(pool);
    }

    @Test
    public void isInSingletonScope() {
        assertThat(provider.getScope()).is(ComponentScope.Singleton);
    }

    @Test
    public void injectsJedisPoolParams() {
        doReturn(JedisPool.class).when(param).getParameterClass();
        assertThat(provider.getInjectable(ic, context, param)).isNotNull();
    }

    @Test
    public void doesNotInjectOtherTypes() {
        doReturn(Jedis.class).when(param).getParameterClass();
        assertThat(provider.getInjectable(ic, context, param)).isNull();
    }

    @Test
    public void injectableReturnsConfiguredPool() {
        doReturn(JedisPool.class).when(param).getParameterClass();
        Injectable injectable = provider.getInjectable(ic, context, param);
        assertThat(injectable.getValue()).is(pool);
    }
}