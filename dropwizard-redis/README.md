Dropwizard Redis Bundle
======================

An addon bundle for using the excellent Jedis redis client in Dropwizard applications.


Dependency Info
---------------

```xml
<dependency>
  <groupId>com.bendb.dropwizard</groupId>
  <artifactId>dropwizard-redis</artifactId>
  <version>1.0.0-0</version>
</dependency>
```


Usage
-----


Add a [JedisFactory](http://droptools.bendb.com/0.7.1-5/apidocs/com/bendb/dropwizard/redis/JedisFactory.html) to your [Configuration](http://dropwizard.io/0.7.1/dropwizard-core/apidocs/io/dropwizard/Configuration.html) class.

```java
@NotNull
@JsonProperty
private JedisFactory redis;

public JedisFactory getJedisFactory() {
	return redis;
}

public void setJedisFactory(JedisFactory jedisFactory) {
	this.redis = jedisFactory;
}
```

Add a [JedisBundle](http://droptools.bendb.com/0.7.1-5/apidocs/com/bendb/dropwizard/redis/JedisBundle.html) to your [Application](http://dropwizard.io/0.7.1/dropwizard-core/apidocs/io/dropwizard/Application.html) class.

```java
@Override
public void initialize(Bootstrap<MyConfiguration> bootstrap) {
    // ...
    bootstrap.addBundle(new JedisBundle<AppConfig>() {
        @Override
        public JedisFactory getJedisFactory(AppConfig configuration) {
            return configuration.getJedisFactory();
        }
    });
}
```

This will enable `@Context` injection of pooled [Jedis](http://todo) and [JedisPool](http://todo) in resource methods:

```java
@GET
@Path("/posts/{id}")
public BlogPost getPost(@QueryParam("id") int postId, @Context Jedis jedis) {
  String cachedBlogContent = jedis.get("post-" + postId);
  // do stuff
  // No need to close the connection, it happens automatically.
}
```

This will also enable redis health-checking.


Configuration
-------------

For configuration the redis connection, there is [JedisFactory](http://droptools.bendb.com/0.7.1-5/apidocs/com/bendb/dropwizard/redis/JedisFactory.html):

```yaml
redis:
  # The redis server's address; required.
  endpoint: localhost:6379
  # Auth password for redis server connection.  (default: null)
  password: null
  # The minimum number of idle connections to maintain in the pool.  (default: 0)
  minIdle: 0
  # The maximum number of idle connections allowed in the pool.  (default: 0)
  maxIdle: 0
  # The maximum number of connections allowed in the pool.  (default: 1024)
  maxTotal: 1924
```

Support
-------

Please file bug reports and feature requests in [GitHub issues](https://github.com/benjamin-bader/droptools/issues).


License
-------

Copyright (c) 2014-2016 Benjamin Bader

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
