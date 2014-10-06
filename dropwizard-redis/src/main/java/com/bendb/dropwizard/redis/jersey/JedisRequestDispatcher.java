package com.bendb.dropwizard.redis.jersey;

import com.google.common.annotations.VisibleForTesting;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class JedisRequestDispatcher implements RequestDispatcher {
    private final Logger LOGGER = LoggerFactory.getLogger(JedisRequestDispatcher.class);

    private final RequestDispatcher dispatcher;

    public JedisRequestDispatcher(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @VisibleForTesting
    RequestDispatcher getUnderlyingDispatcher() {
        return dispatcher;
    }

    @Override
    public void dispatch(Object resource, HttpContext context) {
        try {
            dispatcher.dispatch(resource, context);
        } finally {
            cleanUp(context);
        }
    }

    private void cleanUp(HttpContext context) {
        try {
            Jedis jedis = (Jedis) context.getProperties().remove(Jedis.class.getName());

            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to return a Jedis client to the pool", e);
        }
    }
}
