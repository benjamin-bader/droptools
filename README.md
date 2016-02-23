Droptools
======================

[![Build Status](https://travis-ci.org/benjamin-bader/droptools.svg?branch=master)](https://travis-ci.org/benjamin-bader/droptools)
[![Coverage Status](https://img.shields.io/coveralls/benjamin-bader/droptools.svg)](https://coveralls.io/r/benjamin-bader/droptools)

Useful Dropwizard addons, including `dropwizard-jooq` and `dropwizard-redis`.


[`dropwizard-jooq`](https://github.com/benjamin-bader/droptools/tree/master/dropwizard-jooq)
-----------------

A bundle that adds support for relational database access via the excellent [jOOQ](http://jooq.org) library.


[`dropwizard-redis`](https://github.com/benjamin-bader/droptools/tree/master/dropwizard-redis)
------------------

A bundle that manages a redis connection pool and makes [Jedis](https://github.com/xetorthio/jedis) clients available to resource methods, without the hassle of pooling logic in your code.


Usage
-----

To use the entire `droptools` family:

```xml
<dependency>
  <groupId>com.bendb.dropwizard</groupId>
  <artifactId>droptools-parent</artifactId>
  <version>0.9.1-1</version>
</dependency>
```


Support
-------

Please file bug reports and feature requests in [GitHub issues](https://github.com/benjamin-bader/droptools-parent/issues).


License
-------

Copyright (c) 2014-2015 Benjamin Bader

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
