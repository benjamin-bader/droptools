package com.bendb.dropwizard.redis.jersey;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import redis.clients.jedis.JedisPool;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
public class JedisPoolInjectableProvider implements InjectableProvider<Context, Parameter> {
    private final JedisPool pool;

    public JedisPoolInjectableProvider(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, Context context, Parameter param) {
        if (param.getParameterClass().isAssignableFrom(JedisPool.class)) {
            return new Injectable() {
                @Override
                public Object getValue() {
                    return pool;
                }
            };
        }

        return null;
    }
}
