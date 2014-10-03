package com.bendb.dropwizard.jooq.jersey;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.spi.inject.Injectable;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Context;
import java.util.HashMap;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DSLContextInjectableProviderTest {
    @Mock Configuration configuration;

    DSLContextInjectableProvider provider;

    @Before
    public void setup() throws Exception {
        provider = new DSLContextInjectableProvider(configuration);
    }

    @Test
    public void injectsDSLContextParam() {
        Parameter param = Mockito.mock(Parameter.class);
        Mockito.doReturn(DSLContext.class).when(param).getParameterClass();

        Injectable injectable = provider.getInjectable(Mockito.mock(ComponentContext.class), Mockito.mock(Context.class), param);

        assertThat(injectable.getValue()).isA(DSLContext.class);
    }

    @Test
    public void injectsConfigurationParam() {
        Parameter param = Mockito.mock(Parameter.class);
        Mockito.doReturn(Configuration.class).when(param).getParameterClass();

        Injectable injectable = provider.getInjectable(Mockito.mock(ComponentContext.class), Mockito.mock(Context.class), param);

        assertThat(injectable.getValue()).is(configuration);
    }

    @Test
    public void doesNotInjectOtherTypes() {
        Parameter param = Mockito.mock(Parameter.class);
        Mockito.doReturn(HashMap.class).when(param).getParameterClass();

        Injectable injectable = provider.getInjectable(Mockito.mock(ComponentContext.class), Mockito.mock(Context.class), param);

        assertThat(injectable).isNull();
    }
}