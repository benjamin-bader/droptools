package com.bendb.dropwizard.jooq.jersey;

import com.bendb.dropwizard.common.jersey.DefaultInjectable;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.ws.rs.core.Context;
import java.lang.reflect.Type;

public class DSLContextTypeInjectableProvider implements InjectableProvider<Context, Type> {
    private final Configuration configuration;

    public DSLContextTypeInjectableProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, Context context, Type type) {
        if (type.equals(Configuration.class)) {
            return DefaultInjectable.of(configuration);
        }

        if (type.equals(DSLContext.class)) {
            return DefaultInjectable.of(DSL.using(configuration));
        }

        if (type.equals(ConnectionProvider.class)) {
            return DefaultInjectable.of(configuration.connectionProvider());
        }

        return null;
    }
}
