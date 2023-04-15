package com.bendb.dropwizard.redis;

import io.dropwizard.core.Configuration;

public interface JedisConfiguration<C extends Configuration> {
    JedisFactory getJedisFactory(C configuration);
}
