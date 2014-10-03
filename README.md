Dropwizard jOOQ Bundle
======================

[![Build Status](https://travis-ci.org/benjamin-bader/dropwizard-jooq.svg?branch=master)](https://travis-ci.org/benjamin-bader/dropwizard-jooq)

An addon bundle in the vein of `dropwizard-hibernate` for using the excellent jOOQ SQL library in Dropwizard applications.


Usage
-----

Add a `JooqBundle` to your `Application` class.

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


This will enable `@Context` injection of jOOQ `Configuration` and `DSLContext` parameters in resource methods:

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


Code Generation
---------------

`dropwizard-jooq` provides some facilities for making generated pojos, doas, etc. more convenient to use.

`JodaDateTimeConverter` can be used to map between `java.sql.Timestamp` and Joda `DateTime` objects.  This is currently the only converter bundled; contributions in this area are welcome!


Configuration
-------------

`dropwizard-jooq` uses the same [DataSourceFactory](http://dropwizard.io/0.7.1/dropwizard-db/apidocs/io/dropwizard/db/DataSourceFactory.html) from [`dropwizard-db`](http://dropwizard.io/0.7.1/dropwizard-db/) for configuring its [DataSource](http://docs.oracle.com/javase/7/docs/api/javax/sql/DataSource.html).

For modifying jOOQ configuration settings, there is [FlywayFactory](https://benjamin-bader.github.io/dropwizard-jooq/0.7.0-1/apidocs/com/bendb//dropwizard/jooq/JooqFactory.html):

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

Please file bug reports and feature requests in [GitHub issues](https://github.com/benjamin-bader/dropwizard-jooq/issues).


License
-------

Copyright (c) 2014 Benjamin Bader

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.