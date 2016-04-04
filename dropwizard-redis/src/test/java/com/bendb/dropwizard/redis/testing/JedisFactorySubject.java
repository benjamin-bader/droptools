package com.bendb.dropwizard.redis.testing;

import com.bendb.dropwizard.redis.JedisFactory;
import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;

public class JedisFactorySubject extends Subject<JedisFactorySubject, JedisFactory> {
    public JedisFactorySubject(FailureStrategy failureStrategy, JedisFactory subject) {
        super(failureStrategy, subject);
    }

    public void hasHost(final String host) {
        final JedisFactory subject = getSubject();
        if (subject != null) {
            final String subjectHost = subject.getHost();
            if (host != null && !host.equals(subjectHost)) {
                failWithBadResults("has host", host, "has", subjectHost);
            } else if (host == null && subjectHost != null) {
                failWithBadResults("has host", null, "has", subjectHost);
            }
        } else {
            fail("has host", host);
        }
    }

    public void hasPort(final int port) {
        final JedisFactory subject = getSubject();
        if (subject != null) {
            if (port != subject.getPort()) {
                failWithBadResults("has port", port, "has", subject.getPort());
            }
        } else {
            fail("has port", port);
        }
    }

    public void hasDefaultRedisPort() {
        final JedisFactory subject = getSubject();
        if (subject != null) {
            final int port = subject.getPort();
            if (port != 6379) {
                fail("has the default redis port");
            }
        } else {
            fail("has the default redis port");
        }
    }

    public void hasNullPassword() {
        final JedisFactory subject = getSubject();
        if (subject != null) {
            final String password = subject.getPassword();
            if (password != null) {
                fail("has null password");
            }
        } else {
            fail("has null password");
        }
    }

    public void hasPassword(String passwordToCheck) {
        final JedisFactory subject = getSubject();
        if (subject != null) {
            final String password = subject.getPassword();
            if (!password.equals(passwordToCheck)) {
                fail("has proper password");
            }
        } else {
            fail("has proper password");
        }
    }

    public void hasDefaultMinIdleConnections() {
        final JedisFactory subject = getSubject();
        if (subject != null) {
            final int idle = subject.getMinIdle();
            if (idle != 0) {
                fail("has the default minimum idle redis connections");
            }
        } else {
            fail("has the default minimum idle redis connections");
        }
    }

    public void hasDefaultMaxIdleConnections() {
        final JedisFactory subject = getSubject();
        if (subject != null) {
            final int idle = subject.getMaxIdle();
            if (idle != 0) {
                fail("has the default maximum idle redis connections");
            }
        } else {
            fail("has the default maximum idle redis connections");
        }
    }

    public void hasDefaultTotalConnections() {
        final JedisFactory subject = getSubject();
        if (subject != null) {
            final int total = subject.getMinIdle();
            if (total != 0) {
                fail("has the default total redis connections");
            }
        } else {
            fail("has the default total redis connections");
        }
    }
}
