package com.bendb.dropwizard.jooq.jersey;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.inject.Singleton;
import java.util.Map;
import java.util.function.Function;

@Singleton
public final class DSLContextValueParamProvider implements ValueParamProvider {

    final Map<String, Configuration> configurationMap;

    public DSLContextValueParamProvider(Map<String, Configuration> configurationMap) {
        this.configurationMap = configurationMap;
    }

    @Override
    public Function<ContainerRequest, ?> getValueProvider(Parameter parameter) {
        final Class<?> classType = parameter.getRawType();
        final JooqInject jooqInjectParam = parameter.getAnnotation(JooqInject.class);

        return (classType == null || !classType.equals(DSLContext.class) || jooqInjectParam == null)
                ? null // return null when parameter not supported
                : (req) -> DSL.using(getConfiguration(jooqInjectParam.value()));
    }

    @Override
    public PriorityType getPriority() {
        return ValueParamProvider.Priority.NORMAL;
    }

    private Configuration getConfiguration(
            final String key
    ) {
        return (configurationMap.containsKey(key))
               ? configurationMap.get(key)
               : configurationMap.values().stream().findFirst().orElse(null);
    }
}