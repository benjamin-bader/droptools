package com.bendb.dropwizard.redis.jersey;

import com.google.common.collect.Maps;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JedisInjectableProviderTest {
    @Mock HttpContext httpContext;
    @Mock ComponentContext ic;
    @Mock Context context;
    @Mock Parameter param;
    @Mock JedisPool pool;
    @Mock Jedis jedis;

    private JedisInjectableProvider provider;

    @Before
    public void setup() {
        when(pool.getResource()).thenReturn(jedis);

        provider = new JedisInjectableProvider(pool);
    }

    @Test
    public void isAnnotatedWithProvider() {
        boolean found = false;
        Annotation[] annotations = JedisInjectableProvider.class.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Provider.class) {
                found = true;
                break;
            }
        }
        assertThat(found).named("has @Provides annotation").isTrue();
    }

    @Test
    public void isScopedPerRequest() {
        assertThat(provider.getScope()).is(ComponentScope.PerRequest);
    }

    @Test
    public void injectsJedisParameters() {
        doReturn(Jedis.class).when(param).getParameterClass();
        assertThat(provider.getInjectable(ic, context, param)).isNotNull();
    }

    @Test
    public void doesNotInjectNonJedisParameters() {
        doReturn(JedisPool.class).when(param).getParameterClass();
        assertThat(provider.getInjectable(ic, context, param)).isNull();
    }

    @Test
    public void injectableReturnsPooledJedisConnection() {
        doReturn(Jedis.class).when(param).getParameterClass();
        AbstractHttpContextInjectable injectable = (AbstractHttpContextInjectable) provider.getInjectable(ic, context, param);
        assertThat(injectable.getValue(httpContext)).is(jedis);
    }

    @Test
    public void injectableStoresConnectionInHttpContext() {
        Map<String, Object> properties = Maps.newHashMap();
        when(httpContext.getProperties()).thenReturn(properties);

        doReturn(Jedis.class).when(param).getParameterClass();
        AbstractHttpContextInjectable injectable = (AbstractHttpContextInjectable) provider.getInjectable(ic, context, param);
        Object injectableValue = injectable.getValue(httpContext);

        assertThat(properties).hasKey(Jedis.class.getName()).withValue(injectableValue);
    }
}