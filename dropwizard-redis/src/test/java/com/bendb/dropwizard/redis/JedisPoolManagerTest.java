package com.bendb.dropwizard.redis;

import org.junit.Test;
import redis.clients.jedis.JedisPool;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JedisPoolManagerTest {
    @Test
    public void destroysManagedPool() throws Exception {
        JedisPool pool = mock(JedisPool.class);
        JedisPoolManager manager = new JedisPoolManager(pool);

        manager.stop();

        verify(pool).destroy();
    }
}