package com.bendb.dropwizard.redis;

import com.codahale.metrics.health.HealthCheck;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisHealthCheck extends HealthCheck {
    private final JedisPool pool;

    public JedisHealthCheck(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    protected Result check() throws Exception {
        try (Jedis jedis = pool.getResource()) {
            final String pong = jedis.ping();
            if ("PONG".equals(pong)) {
                return Result.healthy();
            }
        }

        return Result.unhealthy("Could not ping redis");
    }
}
