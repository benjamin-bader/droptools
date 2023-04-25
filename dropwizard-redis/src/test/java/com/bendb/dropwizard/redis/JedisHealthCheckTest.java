package com.bendb.dropwizard.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import static com.bendb.dropwizard.redis.testing.Subjects.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JedisHealthCheckTest {
    @Mock JedisPool pool;
    @Mock Jedis jedis;

    JedisHealthCheck healthcheck;

    @BeforeEach
    public void setup() {
        when(pool.getResource()).thenReturn(jedis);
        healthcheck = new JedisHealthCheck(pool);
    }

    @Test
    public void isHealthyWhenPingCompletes() throws Exception {
        when(jedis.ping()).thenReturn("PONG");
        assertThat(healthcheck.check()).isHealthy();
    }

    @Test
    public void isUnhealthyWhenPingFails() throws Exception {
        when(jedis.ping()).thenReturn("huh?");
        assertThat(healthcheck.check()).isUnhealthy();
    }

    @Test
    public void doesNotCatchJedisExceptions() throws Exception {
        when(jedis.ping()).thenThrow(new JedisException("boom"));
        Assertions.assertThrows(JedisException.class, healthcheck::check);
    }

    @Test
    public void doesNotCatchJedisPoolExceptions() throws Exception {
        when(pool.getResource()).thenThrow(new JedisConnectionException("nope."));
        Assertions.assertThrows(
            JedisConnectionException.class,
            healthcheck::check);
    }

    @Test
    public void returnsConnectionWhenComplete() throws Exception {
        when(jedis.ping()).thenReturn("PONG");
        healthcheck.check();
        verify(jedis).close();
    }

    @Test
    public void returnsConnectionWhenPingThrows() throws Exception {
        when(jedis.ping()).thenThrow(new JedisException("boom"));
        Assertions.assertThrows(JedisException.class, healthcheck::check);
        verify(jedis).close();
    }
}