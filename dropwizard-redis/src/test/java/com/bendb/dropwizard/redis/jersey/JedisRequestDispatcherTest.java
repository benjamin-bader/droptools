package com.bendb.dropwizard.redis.jersey;

import com.google.common.collect.Maps;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import redis.clients.jedis.Jedis;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assert_;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JedisRequestDispatcherTest {
    @Mock HttpContext httpContext;
    @Mock RequestDispatcher delegate;
    @Mock Jedis jedis;

    private Object resource;
    private Map<String, Object> properties;
    private JedisRequestDispatcher dispatcher;

    @Before
    public void setup() {
        resource = new Object();
        properties = Maps.newHashMap();
        dispatcher = new JedisRequestDispatcher(delegate);

        when(httpContext.getProperties()).thenReturn(properties);
    }

    @Test
    public void dispatchesToDelegate() {
        dispatcher.dispatch(resource, httpContext);
        verify(delegate).dispatch(resource, httpContext);
    }

    @Test
    public void closesJedisClientInHttpContext() {
        properties.put(Jedis.class.getName(), jedis);
        dispatcher.dispatch(resource, httpContext);
        verify(jedis).close();
    }

    @Test
    public void removesClosedJedisClientFromContext() {
        properties.put(Jedis.class.getName(), jedis);
        dispatcher.dispatch(resource, httpContext);
        assertThat(properties).lacksKey(Jedis.class.getName());
    }

    @Test
    public void closesJedisClientInHttpContextWhenDispatchFails() {
        RuntimeException exception = new RuntimeException("BAM");
        doThrow(exception).when(delegate).dispatch(resource, httpContext);

        properties.put(Jedis.class.getName(), jedis);
        try {
            dispatcher.dispatch(resource, httpContext);
            assert_().fail("Expected a RuntimeException, got none");
        } catch (RuntimeException e) {
            assertThat(e).is(exception);
            verify(jedis).close();
        }
    }
}