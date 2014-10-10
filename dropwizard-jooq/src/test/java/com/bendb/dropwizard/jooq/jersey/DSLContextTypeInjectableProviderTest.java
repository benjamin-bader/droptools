package com.bendb.dropwizard.jooq.jersey;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import io.dropwizard.db.DataSourceFactory;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Context;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DSLContextTypeInjectableProviderTest {
    @Mock Configuration configuration;
    @Mock ConnectionProvider connectionProvider;
    @Mock ComponentContext ic;
    @Mock Context annotation;

    DSLContextTypeInjectableProvider provider;

    @Before
    public void setup() throws Exception {
        when(configuration.connectionProvider()).thenReturn(connectionProvider);
        provider = new DSLContextTypeInjectableProvider(configuration);
    }

    @Test
    public void isScopedAsSingleton() {
        assertThat(provider.getScope()).is(ComponentScope.Singleton);
    }

    @Test
    public void injectsDSLContexts() {
        assertThat(provider.getInjectable(ic, annotation, DSLContext.class)).isNotNull();
    }

    @Test
    public void injectsJooqConfigurations() {
        assertThat(provider.getInjectable(ic, annotation, Configuration.class)).isNotNull();
    }

    @Test
    public void injectsConnectionProviders() {
        assertThat(provider.getInjectable(ic, annotation, ConnectionProvider.class)).isNotNull();
    }

    @Test
    public void doesNotInjectOtherTypes() {
        assertThat(provider.getInjectable(ic, annotation, DataSourceFactory.class)).isNull();
    }
}