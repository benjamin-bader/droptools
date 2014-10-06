package com.bendb.dropwizard.redis.jersey;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JedisResourceMethodDispatchProviderTest {
    @Mock ResourceMethodDispatchProvider delegate;
    @Mock RequestDispatcher dispatcher;
    @Mock AbstractResourceMethod method;

    private JedisResourceMethodDispatchProvider provider;

    @Before
    public void setup() {
        provider = new JedisResourceMethodDispatchProvider(delegate);
    }

    @Test
    public void wrapsReturnedDispatcher() {
        when(delegate.create(method)).thenReturn(dispatcher);

        RequestDispatcher requestDispatcher = provider.create(method);

        JedisRequestDispatcher jedisRequestDispatcher = (JedisRequestDispatcher) requestDispatcher;
        assertThat(jedisRequestDispatcher.getUnderlyingDispatcher()).is(dispatcher);
    }

    @Test
    public void returnsNullWhenDelegateReturnsNull() {
        assertThat(provider.create(method)).isNull();
    }
}