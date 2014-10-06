package com.bendb.dropwizard.redis.testing;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;

public class HealthCheckResultSubject extends Subject<HealthCheckResultSubject, HealthCheck.Result> {
    public HealthCheckResultSubject(FailureStrategy failureStrategy, HealthCheck.Result subject) {
        super(failureStrategy, subject);
    }

    public void isHealthy() {
        HealthCheck.Result result = getSubject();
        if (result != null) {
            if (!result.isHealthy()) {
                fail("is healthy");
            }
        } else {
            fail("is healthy");
        }
    }

    public void isUnhealthy() {
        HealthCheck.Result result = getSubject();
        if (result != null) {
            if (result.isHealthy()) {
                fail("is unhealthy");
            }
        } else {
            fail("is unhealthy");
        }
    }
}
