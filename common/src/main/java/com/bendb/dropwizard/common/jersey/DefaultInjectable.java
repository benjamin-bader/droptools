package com.bendb.dropwizard.common.jersey;

import com.sun.jersey.spi.inject.Injectable;

/**
 * A simple implementation of {@link com.sun.jersey.spi.inject.Injectable} that provides a singleton value.
 *
 * @param <T> the type of value to be injected
 */
public class DefaultInjectable<T> implements Injectable<T> {
    private final T value;

    public DefaultInjectable(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    public static <E> DefaultInjectable<E> of(E value) {
        return new DefaultInjectable<>(value);
    }
}
