package com.bendb.dropwizard.jooq.jersey;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

/**
 * An {@link com.sun.jersey.spi.inject.InjectableProvider} that can inject {@link org.jooq.Configuration},
 * {@link org.jooq.DSLContext}, and {@link org.jooq.ConnectionProvider} values.
 */
@Provider
public class DSLContextInjectableProvider implements InjectableProvider<Context, Parameter> {
    private final Configuration configuration;

    public DSLContextInjectableProvider(Configuration configuration) {
        this.configuration = Preconditions.checkNotNull(configuration, "configuration");
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, Context context, Parameter parameter) {
        if (parameter.getParameterClass().isAssignableFrom(DSLContext.class)){
            return new DefaultInjectable<>(DSL.using(configuration));
        }

        if (parameter.getParameterClass().isAssignableFrom(Configuration.class)) {
            return new DefaultInjectable<>(configuration);
        }

        if (parameter.getParameterClass().isAssignableFrom(ConnectionProvider.class)) {
            return new DefaultInjectable<>(configuration.connectionProvider());
        }

        return null;
    }
}
