package com.bendb.dropwizard.redis;

import com.bendb.dropwizard.redis.jersey.JedisFactory;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import redis.clients.jedis.JedisPool;

public abstract class JedisBundle<C extends Configuration>
        implements ConfiguredBundle<C>, JedisConfiguration<C> {
    private JedisPool pool;

    public JedisPool getPool() {
        return pool;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(C configuration, Environment environment) throws Exception {
        pool = getJedisFactory(configuration).build(environment);

        environment.healthChecks().register("redis", new JedisHealthCheck(pool));
        environment.jersey().register(new JedisFactory(pool));
    }
}
