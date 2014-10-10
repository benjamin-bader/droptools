package com.bendb.dropwizard.jooq.jersey;

import com.bendb.dropwizard.common.jersey.DefaultInjectable;
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
 * An {@link InjectableProvider} that can inject {@link Configuration},
 * {@link DSLContext}, and {@link ConnectionProvider} values into resource
 * method parameters.
 */
@Provider
public class DSLContextParameterInjectableProvider implements InjectableProvider<Context, Parameter> {
    private final Configuration configuration;

    public DSLContextParameterInjectableProvider(Configuration configuration) {
        this.configuration = Preconditions.checkNotNull(configuration, "configuration");
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, Context context, Parameter parameter) {
        final Class<?> parameterClass = parameter.getParameterClass();
        if (parameterClass.isAssignableFrom(DSLContext.class)){
            return DefaultInjectable.of(DSL.using(configuration));
        }

        if (parameterClass.isAssignableFrom(Configuration.class)) {
            return DefaultInjectable.of(configuration);
        }

        if (parameterClass.isAssignableFrom(ConnectionProvider.class)) {
            return DefaultInjectable.of(configuration.connectionProvider());
        }

        return null;
    }
}
