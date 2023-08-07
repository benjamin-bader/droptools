Droptools
======================

[![Pre-merge checks](https://github.com/benjamin-bader/droptools/actions/workflows/ci.yaml/badge.svg)](https://github.com/benjamin-bader/droptools/actions/workflows/ci.yaml)
[![Coverage Status](https://img.shields.io/coveralls/benjamin-bader/droptools.svg)](https://coveralls.io/r/benjamin-bader/droptools)

Useful Dropwizard addons, including `dropwizard-jooq` and `dropwizard-redis`.

I, [daberkow](https://github.com/daberkow/), have patched the library to support Dropwizard 3.0 and Java 11+. I am not using `dropwizard-redis`, that has been patched to pass builds, yet I can not promise any other operations.

[`dropwizard-jooq`](docs/jooq.md)
-----------------

A bundle that adds support for relational database access via the excellent [jOOQ](http://jooq.org) library.


[`dropwizard-redis`](docs/redis.md)
------------------

A bundle that manages a redis connection pool and makes [Jedis](https://github.com/xetorthio/jedis) clients available to resource methods, without the hassle of pooling logic in your code.

Version Matrix
--------------

jOOQ only supports certain versions of Java with the open source edition. Dropwizard also now has version 3 which maintains the `javax` namespace, and version 4 which goes ot `jakarta`.

jOOQ 3.16 is the last version which supports Java 11. jOOQ 3.17+ supports Java 17+. Dropwizard 3.0 and 4.0 requires Java 11. This current build is compiled against jOOQ 3.16 and Dropwizard 3.0; making it Java 8+ compatible with `javax` as the namespace.

Support
-------

Please file bug reports and feature requests in [GitHub issues](https://github.com/benjamin-bader/droptools-parent/issues).


License
-------

Copyright (c) 2014-2023 Benjamin Bader

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
