package com.bendb.dropwizard.redis.jersey;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Bind a JedisPool instance, as configured in Dropwizard, and a
 * HK2 {@link org.glassfish.hk2.api.Factory} to create injectable
 * Jedis.
 */
public class JedisPoolBinder extends AbstractBinder {
    private final JedisPool pool;

    public JedisPoolBinder(JedisPool pool) { this.pool = pool; }

    @Override
    protected void configure() {
        // Always return the same pool when/where ever it's asked for
        bind(pool).to(JedisPool.class);

        bindFactory(new JedisFactory(pool))
                .to(Jedis.class)
                .in(RequestScoped.class);
    }
}
