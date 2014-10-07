package com.bendb.dropwizard.common.jersey;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

import java.lang.annotation.Annotation;

/**
 * An {@link InjectableProvider} that injects parameters of a single type.
 *
 * @param <A> the injectable parameter's decorating annotation.
 * @param <T> the type of value to be injected.
 */
public abstract class TypedParamInjectableProvider<A extends Annotation, T> implements InjectableProvider<A, Parameter> {
    private final Class<T> clazz;

    protected TypedParamInjectableProvider(Class<T> clazz) {
        this.clazz = Preconditions.checkNotNull(clazz, "clazz");
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, A a, Parameter parameter) {
        if (parameter.getParameterClass().isAssignableFrom(clazz)) {
            return getTypedInjectable(ic, a);
        } else {
            return null;
        }
    }

    protected abstract Injectable<T> getTypedInjectable(ComponentContext ic, A annotation);
}
