package com.bendb.dropwizard.redis.jersey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class JedisFactoryTest {
    @Mock JedisPool pool;
    @Mock Jedis jedis;

    private JedisFactory factory;

    @BeforeEach
    public void setup() {
        lenient().when(pool.getResource()).thenReturn(jedis);
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
