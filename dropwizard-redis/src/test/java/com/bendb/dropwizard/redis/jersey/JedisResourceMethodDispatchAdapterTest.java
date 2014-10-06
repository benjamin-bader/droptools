package com.bendb.dropwizard.redis.jersey;

import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

public class JedisResourceMethodDispatchAdapterTest {
    @Test
    public void returnsJedisDispatchProvider() {
        ResourceMethodDispatchAdapter adapter = new JedisResourceMethodDispatchAdapter();
        ResourceMethodDispatchProvider provider = mock(ResourceMethodDispatchProvider.class);

        assertThat(adapter.adapt(provider)).isA(JedisResourceMethodDispatchProvider.class);
    }
}