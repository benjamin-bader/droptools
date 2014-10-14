package com.bendb.dropwizard.common.jersey;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import org.junit.Test;

import javax.ws.rs.core.Context;

import static com.google.common.truth.Truth.assertThat;

public class PerRequestParamInjectableProviderTest {
    @Test
    public void isPerRequestScope() {
        PerRequestParamInjectableProvider<Context, Object> provider =
                new PerRequestParamInjectableProvider<Context, Object>(Object.class) {
                    @Override
                    protected Injectable<Object> getTypedInjectable(ComponentContext ic, Context annotation) {
                        return null;
                    }
                };

        assertThat(provider.getScope()).isEqualTo(ComponentScope.PerRequest);
    }
}