package com.bendb.dropwizard.redis.testing;

import com.bendb.dropwizard.redis.JedisFactory;
import com.codahale.metrics.health.HealthCheck;

public class Subjects {
    public static HealthCheckResultSubject assertThat(HealthCheck.Result result) {
        return HealthCheckResultSubject.assertThat(result);
    }

    public static JedisFactorySubject assertThat(JedisFactory factory) {
        return JedisFactorySubject.assertThat(factory);
    }
}
