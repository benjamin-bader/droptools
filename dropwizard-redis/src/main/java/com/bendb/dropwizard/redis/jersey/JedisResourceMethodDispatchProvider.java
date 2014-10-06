package com.bendb.dropwizard.redis.jersey;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

import javax.ws.rs.ext.Provider;

@Provider
public class JedisResourceMethodDispatchProvider implements ResourceMethodDispatchProvider {

    private final ResourceMethodDispatchProvider provider;

    public JedisResourceMethodDispatchProvider(ResourceMethodDispatchProvider provider) {
        this.provider = provider;
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod method) {
        RequestDispatcher dispatcher = provider.create(method);

        if (dispatcher == null) {
            return null;
        }

        return new JedisRequestDispatcher(dispatcher);
    }
}
