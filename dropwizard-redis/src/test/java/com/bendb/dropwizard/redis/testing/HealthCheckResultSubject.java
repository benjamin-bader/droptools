package com.bendb.dropwizard.redis.testing;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.truth.Fact;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;

import javax.annotation.Nullable;

import static com.google.common.truth.Truth.assertAbout;

public class HealthCheckResultSubject extends Subject {

    public static HealthCheckResultSubject assertThat(@Nullable HealthCheck.Result result) {
        return assertAbout(SUBJECT_FACTORY).that(result);
    }

    private static final Subject.Factory<HealthCheckResultSubject, HealthCheck.Result> SUBJECT_FACTORY
            = HealthCheckResultSubject::new;

    @Nullable
    private final HealthCheck.Result actual;

    HealthCheckResultSubject(FailureMetadata metadata, @Nullable HealthCheck.Result subject) {
        super(metadata, subject);
        this.actual = subject;
    }

    public void isHealthy() {
        if (actual == null || !actual.isHealthy()) {
            failWithActual(Fact.simpleFact("expected to be healthy"));
        }
    }

    public void isUnhealthy() {
        if (actual == null || actual.isHealthy()) {
            failWithActual(Fact.simpleFact("expected to be unhealthy"));
        }
    }
}
