package com.bendb.dropwizard.redis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.HostAndPort;
import io.dropwizard.setup.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * A factory for creating configured {@link JedisPool} instances.
 *
 * <p/>
 * <strong>Configuration Parameters</strong>
 * <table>
 *     <tr>
 *         <th>Name</th>
 *         <th>Default Value</th>
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td>endpoint</td>
 *         <td><code>[required]</code></td>
 *         <td>
 *             The redis server's host and port, i.e. <code>localhost:6379</code>.
 *             If no port is given, the default redis port {@value #DEFAULT_PORT} is assumed.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>password</td>
 *         <td><code>null</code></td>
 *         <td>password: [null] - Auth password for redis server connection; Default is null.</td>
 *     </tr>
 *     <tr>
 *         <td>minIdle</td>
 *         <td><code>0</code></td>
 *         <td>The minimum number of idle connections to maintain in the connection pool.</td>
 *     </tr>
 *     <tr>
 *         <td>maxIdle</td>
 *         <td><code>0</code></td>
 *         <td>The maximum number of idle connections to maintain in the connection pool.</td>
 *     </tr>
 *     <tr>
 *         <td>maxTotal</td>
 *         <td><code>{@value JedisFactory#DEFAULT_MAX_TOTAL}</code></td>
 *         <td>The maximum number of connections allowed in the connection pool.</td>
 *     </tr>
 * </table>
 */
public class JedisFactory {
    public static final int DEFAULT_PORT = 6379;
    public static final int DEFAULT_MAX_TOTAL = 1024;

    @JsonProperty
    @NotNull
    private HostAndPort endpoint;

    @JsonProperty
    private String password;

    @JsonProperty
    private int minIdle = 0;

    @JsonProperty
    private int maxIdle = 0;

    @JsonProperty
    private int maxTotal = DEFAULT_MAX_TOTAL;

    @JsonProperty
    public void setUrl(URL url) {
        this.endpoint = HostAndPort.fromParts(url.getHost(), url.getPort());
        String userInfo = url.getUserInfo();
        if (userInfo != null && !userInfo.isEmpty()) {
            String[] credentials = userInfo.split(":");
            this.password = credentials[1];
        }
    }

    public HostAndPort getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(HostAndPort endpoint) {
        this.endpoint = endpoint;
    }

    public String getHost() {
        return endpoint.getHostText();
    }

    public int getPort() {
        return endpoint.getPortOrDefault(DEFAULT_PORT);
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public JedisPool build(Environment environment) {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMinIdle(getMinIdle());
        poolConfig.setMaxIdle(getMaxIdle());
        poolConfig.setMaxTotal(getMaxTotal());

        final JedisPool pool = new JedisPool(poolConfig, getHost(), getPort(), Protocol.DEFAULT_TIMEOUT, getPassword());

        environment.lifecycle().manage(new JedisPoolManager(pool));

        return pool;
    }
}
