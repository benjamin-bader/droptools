package com.bendb.dropwizard.jooq.jersey;

import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public final class DSLContextValueFactoryProvider implements ValueFactoryProvider {

    final Map<String, Configuration> configurationMap;

    public DSLContextValueFactoryProvider(Map<String, Configuration> configurationMap) {
        this.configurationMap = configurationMap;
    }

    @Override
    public Factory<?> getValueFactory(Parameter parameter) {
        final Class<?> classType = parameter.getRawType();
        final JooqInject jooqInjectParam = parameter.getAnnotation(JooqInject.class);

        return (classType == null || !classType.equals(DSLContext.class) || jooqInjectParam == null)
               ? null // return null when parameter not supported
               : new DSLContextFactory(getConfiguration(configurationMap, jooqInjectParam.value())
               );
    }

    @Override
    public PriorityType getPriority() {
        return ValueFactoryProvider.Priority.NORMAL;
    }

    private Configuration getConfiguration(
            final Map<String, Configuration> configurationMap,
            final String key
    ) {
        return (configurationMap.containsKey(key))
               ? configurationMap.get(key)
               : configurationMap.values().stream().findFirst().orElse(null);
    }
}