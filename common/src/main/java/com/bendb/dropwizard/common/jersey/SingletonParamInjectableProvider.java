package com.bendb.dropwizard.common.jersey;

import com.sun.jersey.core.spi.component.ComponentScope;

import java.lang.annotation.Annotation;

/**
 * A strongly-typed {@link com.sun.jersey.spi.inject.InjectableProvider}
 * that injects singleton-scoped values.
 *
 * @param <A> the injectable parameter's decorating annotation
 * @param <T> the type of value to be injected
 */
public abstract class SingletonParamInjectableProvider<A extends Annotation, T> extends TypedParamInjectableProvider<A, T> {
    protected SingletonParamInjectableProvider(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }
}
