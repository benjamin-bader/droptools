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
}
