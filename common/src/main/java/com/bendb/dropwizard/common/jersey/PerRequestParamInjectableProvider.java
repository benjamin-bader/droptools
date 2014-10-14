package com.bendb.dropwizard.common.jersey;

import com.sun.jersey.core.spi.component.ComponentScope;

import java.lang.annotation.Annotation;

/**
 * A strongly-typed {@link com.sun.jersey.spi.inject.InjectableProvider}
 * whose values are injected per-request.
 *
 * @param <A> the injectable parameter's decorating annotation
 * @param <T> the type of value to be injected
 */
public abstract class PerRequestParamInjectableProvider<A extends Annotation, T> extends TypedParamInjectableProvider<A, T> {
    protected PerRequestParamInjectableProvider(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }
}
