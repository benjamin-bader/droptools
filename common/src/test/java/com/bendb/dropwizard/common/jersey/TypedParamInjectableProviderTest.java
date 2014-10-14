package com.bendb.dropwizard.common.jersey;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import org.junit.Test;

import javax.ws.rs.core.Context;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class TypedParamInjectableProviderTest {
    @Test(expected = NullPointerException.class)
    public void requiresNonNullClass() {
        new TypedParamInjectableProvider<Context, Object>(null) {
            @Override
            protected Injectable<Object> getTypedInjectable(ComponentContext ic, Context annotation) {
                return null;
            }

            @Override
            public ComponentScope getScope() {
                return null;
            }
        };
    }

    @Test
    public void injectsParameterOfCorrectType() {
        ComponentContext ic = mock(ComponentContext.class);
        Context anno = mock(Context.class);
        Parameter param = mock(Parameter.class);
        doReturn(Object.class).when(param).getParameterClass();

        assertThat(new ObjectInjectableProvider().getInjectable(ic, anno, param)).isNotNull();
    }

    @Test
    public void doesNotInjectIncorrectType() {
        ComponentContext ic = mock(ComponentContext.class);
        Context anno = mock(Context.class);
        Parameter param = mock(Parameter.class);
        doReturn(Integer.class).when(param).getParameterClass();

        assertThat(new ObjectInjectableProvider().getInjectable(ic, anno, param)).isNull();
    }

    static class ObjectInjectableProvider extends TypedParamInjectableProvider<Context, Object> {
        public ObjectInjectableProvider() {
            super(Object.class);
        }

        @Override
        protected Injectable<Object> getTypedInjectable(ComponentContext ic, Context annotation) {
            return new DefaultInjectable<>(new Object());
        }

        @Override
        public ComponentScope getScope() {
            return null;
        }
    }
}