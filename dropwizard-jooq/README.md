Dropwizard jOOQ Bundle
======================

[![Build Status](https://travis-ci.org/benjamin-bader/droptools.svg?branch=master)](https://travis-ci.org/benjamin-bader/droptools)
[![Coverage Status](https://img.shields.io/coveralls/benjamin-bader/droptools.svg)](https://coveralls.io/r/benjamin-bader/droptools?branch=master)

An addon bundle in the vein of `dropwizard-hibernate` for using the excellent jOOQ SQL library in Dropwizard applications.


Dependency Info
---------------

```xml
<dependency>
  <groupId>com.bendb.dropwizard</groupId>
  <artifactId>dropwizard-jooq</artifactId>
  <version>0.8.1-0</version>
</dependency>
```


Usage
-----

Add a [JooqBundle](http://droptools.bendb.com/0.8.1-0/apidocs/com/bendb/dropwizard/jooq/JooqFactory.html) to your [Application](http://dropwizard.io/0.8.1/dropwizard-core/apidocs/io/dropwizard/Application.html) class.

```java
@Override
public void initialize(Bootstrap<MyConfiguration> bootstrap) {
    // ...
    bootstrap.addBundle(new JooqBundle<AppConfig>() {
        @Override
        public DataSourceFactory getDataSourceFactory(AppConfig configuration) {
            return configuration.getDataSourceFactory();
        }

        @Override
        public JooqFactory getJooqFactory(AppConfig configuration) {
            return configuration.getJooqFactory();
        }
    });
}
```


This will enable `@Context` injection of jOOQ [Configuration](http://www.jooq.org/javadoc/3.4.0/org/jooq/Configuration.html) and [DSLContext](http://www.jooq.org/javadoc/3.4.0/org/jooq/DSLContext.html) parameters in resource methods:

```java
@GET
@Path("/posts/{id}")
public BlogPost getPost(@QueryParam("id") int postId, @Context DSLContext database) {
    BlogPostRecord post = database
        .selectFrom(POST)
        .where(POST.ID.equal(postId))
        .fetchOne();

    // do stuff
}
```

This will also enable database healthchecks and install exception mappers.

Finally, because I <3 postgres and jOOQ can lag behind some of its features, [PostgresSupport](http://droptools.bendb.com/0.8.1-0/apidocs/com/bendb/dropwizard/jooq/PostgresSupport.html) provides a few helpers for aggregating array values in queries.

For example (taken from the sample project):

```java
import static com.bendb.dropwizard.jooq.PostgresSupport.arrayAgg;

database
    .select(BLOG_POST.ID, BLOG_POST.BODY, BLOG_POST.CREATED_AT, arrayAgg(POST_TAG.TAG_NAME))
    .from(BLOG_POST)
    .leftOuterJoin(POST_TAG)
    .on(BLOG_POST.ID.equal(POST_TAG.POST_ID))
    .where(BLOG_POST.ID.equal(id.get()))
    .groupBy(BLOG_POST.ID, BLOG_POST.BODY, BLOG_POST.CREATED_AT)
    .fetchOne();
```


Code Generation
---------------

[`dropwizard-jooq`](http://droptools.bendb.com/) provides some classes for making generated pojos, DAOs, etc. more convenient to use.

[JodaDateTimeConverter](http://droptools.bendb.com/0.8.1-0/apidocs/com/bendb/dropwizard/jooq/JooqFactory.html) can be used to map between `java.sql.Timestamp` and Joda `DateTime` objects.  This is currently the only converter bundled; contributions in this area are welcome!


Configuration
-------------

[`dropwizard-jooq`](http://droptools.bendb.com/) uses the same [DataSourceFactory](http://dropwizard.io/0.8.1/dropwizard-db/apidocs/io/dropwizard/db/DataSourceFactory.html) from [`dropwizard-db`](http://dropwizard.io/0.8.1/dropwizard-db/) for configuring its [DataSource](http://docs.oracle.com/javase/7/docs/api/javax/sql/DataSource.html).

For modifying jOOQ configuration settings, there is [JooqFactory](http://droptools.bendb.com/0.8.1-0/apidocs/com/bendb/dropwizard/jooq/JooqFactory.html):

```yaml
jooq:
  # The flavor of SQL to generate. If not specified, it will be inferred from the JDBC connection URL.  (default: null)
  dialect: POSTGRES
  # Whether to write generated SQL to a logger before execution.  (default: no)
  logExecutedSql: yes
  # Whether to include schema names in generated SQL.  (default: yes)
  renderSchema: yes
  # How names should be rendered in generated SQL.  One of QUOTED, AS_IS, LOWER, or UPPER.  (default: QUOTED)
  renderNameStyle: QUOTED
  # How keywords should be rendered in generated SQL.  One of LOWER, UPPER.  (default: UPPER)
  renderKeywordStyle: LOWER
  # Whether generated SQL should be pretty-printed.  (default: no)
  renderFormatted: no
  # How parameters should be represented.  One of INDEXED, NAMED, or INLINE.  (default: INDEXED)
  paramType: INDEXED
  # How statements should be generated; one of PREPARED_STATEMENT or STATIC_STATEMENT.  (default: PREPARED_STATEMENT)
  statementType: PREPARED_STATEMENT
  # Whether internal jOOQ logging should be enabled.  (default: no)
  executeLogging: no
  # Whether optimistic locking should be enabled.  (default: no)
  executeWithOptimisticLocking: no
  # Whether returned records should be 'attached' to the jOOQ context.  (default: yes)
  attachRecords: yes
  # Whether primary-key fields should be updatable.  (default: no)
  updatablePrimaryKeys: no
```

Support
-------

Please file bug reports and feature requests in [GitHub issues](https://github.com/benjamin-bader/droptools/issues).


License
-------

Copyright (c) 2014-2015 Benjamin Bader

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
