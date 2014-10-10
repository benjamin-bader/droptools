package com.bendb.dropwizard.redis.jersey;

import com.bendb.dropwizard.common.jersey.DefaultInjectable;
import com.bendb.dropwizard.common.jersey.SingletonParamInjectableProvider;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.spi.inject.Injectable;
import redis.clients.jedis.JedisPool;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
public class JedisPoolInjectableProvider extends SingletonParamInjectableProvider<Context, JedisPool> {
    private final JedisPool pool;

    public JedisPoolInjectableProvider(JedisPool pool) {
        super(JedisPool.class);
        this.pool = pool;
    }

    @Override
    protected Injectable<JedisPool> getTypedInjectable(ComponentContext ic, Context annotation) {
        return new DefaultInjectable<>(pool);
    }
}
