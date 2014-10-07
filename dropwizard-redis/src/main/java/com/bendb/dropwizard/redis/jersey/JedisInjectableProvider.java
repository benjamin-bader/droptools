package com.bendb.dropwizard.redis.jersey;

import com.bendb.dropwizard.common.jersey.PerRequestParamInjectableProvider;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;

@Provider
public class JedisInjectableProvider extends PerRequestParamInjectableProvider<Context, Jedis> {
    private final class JedisInjectable extends AbstractHttpContextInjectable<Jedis> {
        private final JedisPool pool;

        private JedisInjectable(JedisPool pool) {
            this.pool = pool;
        }

        @Override
        public Jedis getValue(HttpContext c) {
            Jedis jedis = pool.getResource();

            c.getProperties().put(Jedis.class.getName(), jedis);

            return jedis;
        }
    }

    private final JedisPool pool;

    public JedisInjectableProvider(JedisPool pool) {
        super(Jedis.class);
        this.pool = pool;
    }

    @Override
    public Injectable<Jedis> getInjectable(ComponentContext ic, Context context) {
        return new JedisInjectable(pool);
    }
}
