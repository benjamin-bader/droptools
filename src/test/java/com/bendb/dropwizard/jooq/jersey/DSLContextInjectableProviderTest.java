package com.bendb.dropwizard.jooq.jersey;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.spi.inject.Injectable;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Context;
import java.util.HashMap;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DSLContextInjectableProviderTest {
    @Mock Configuration configuration;
    @Mock ConnectionProvider connectionProvider;

    DSLContextInjectableProvider provider;

    @Before
    public void setup() throws Exception {
        when(configuration.connectionProvider()).thenReturn(connectionProvider);
        provider = new DSLContextInjectableProvider(configuration);
    }

    @Test
    public void injectsDSLContextParam() {
        Parameter param = mock(Parameter.class);
        doReturn(DSLContext.class).when(param).getParameterClass();

        Injectable injectable = provider.getInjectable(mock(ComponentContext.class), mock(Context.class), param);

        assertThat(injectable.getValue()).isA(DSLContext.class);
    }

    @Test
    public void injectsConfigurationParam() {
        Parameter param = mock(Parameter.class);
        doReturn(Configuration.class).when(param).getParameterClass();

        Injectable injectable = provider.getInjectable(mock(ComponentContext.class), mock(Context.class), param);

        assertThat(injectable.getValue()).is(configuration);
    }

    @Test
    public void injectsConnectionProvider() {
        Parameter param = mock(Parameter.class);
        doReturn(ConnectionProvider.class).when(param).getParameterClass();

        Injectable injectable = provider.getInjectable(mock(ComponentContext.class), mock(Context.class), param);

        assertThat(injectable.getValue()).is(connectionProvider);
    }

    @Test
    public void doesNotInjectOtherTypes() {
        Parameter param = mock(Parameter.class);
        doReturn(HashMap.class).when(param).getParameterClass();

        Injectable injectable = provider.getInjectable(mock(ComponentContext.class), mock(Context.class), param);

        assertThat(injectable).isNull();
    }
}