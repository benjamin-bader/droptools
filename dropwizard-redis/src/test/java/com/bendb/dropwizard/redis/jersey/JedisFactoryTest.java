package com.bendb.dropwizard.redis.jersey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class JedisFactoryTest {
    @Mock JedisPool pool;
    @Mock Jedis jedis;

    private JedisFactory factory;

    @Before
    public void setup() {
        when(pool.getResource()).thenReturn(jedis);
        factory = new JedisFactory(pool);
    }

    @Test
    public void providesAJedisClientInstance() {
        assertThat(factory.provide()).isInstanceOf(Jedis.class);
        verify(pool).getResource();
    }

    @Test
    public void disposesAJedisConnectionProperly() {
        factory.dispose(jedis);
        verify(jedis).close();
    }
}
