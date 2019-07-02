package com.bendb.dropwizard.redis.testing;

import com.bendb.dropwizard.redis.JedisFactory;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;

import static com.google.common.truth.Truth.assertAbout;

public class JedisFactorySubject extends Subject {

    public static JedisFactorySubject assertThat(JedisFactory actual) {
        return assertAbout(SUBJECT_FACTORY).that(actual);
    }

    private static final Subject.Factory<JedisFactorySubject, JedisFactory> SUBJECT_FACTORY = JedisFactorySubject::new;

    private final JedisFactory actual;

    public JedisFactorySubject(FailureMetadata failureMetadata, JedisFactory actual) {
        super(failureMetadata, actual);
        this.actual = actual;
    }

    public void hasHost(final String host) {
        check("getHost()").that(actual.getHost()).isEqualTo(host);
    }

    public void hasPort(final int port) {
        check("getPort()").that(actual.getPort()).isEqualTo(port);
    }

    public void hasDefaultRedisPort() {
        check("getPort()").that(actual.getPort()).isEqualTo(6379);
    }

    public void hasNullPassword() {
        check("getPassword()").that(actual.getPassword()).isNull();
    }

    public void hasPassword(String passwordToCheck) {
        check("getPassword()").that(actual.getPassword()).isEqualTo(passwordToCheck);
    }

    public void hasSsl(boolean sslToCheck) {
        check("getSsl()").that(actual.getSsl()).isTrue();
    }

    public void hasDefaultMinIdleConnections() {
        check("getMinIdle()").that(actual.getMinIdle()).isEqualTo(0);
    }

    public void hasDefaultMaxIdleConnections() {
        check("getMaxIdle()").that(actual.getMaxIdle()).isEqualTo(0);
    }

    public void hasDefaultTotalConnections() {
        check("getMaxTotal()").that(actual.getMaxTotal()).isEqualTo(1024);
    }
}
