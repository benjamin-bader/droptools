package com.bendb.dropwizard.redis.jersey;

import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;

public class JedisResourceMethodDispatchAdapter implements ResourceMethodDispatchAdapter {
    @Override
    public ResourceMethodDispatchProvider adapt(ResourceMethodDispatchProvider provider) {
        return new JedisResourceMethodDispatchProvider(provider);
    }
}
