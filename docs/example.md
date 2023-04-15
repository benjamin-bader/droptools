Dropwizard jOOQ Example
=======================

The example is used to test the `dropwizard-jooq` functionality with a simple Dropwizard app.

Setup
-----

1. Install any version of postgres, the only limit is if [jOOQ limits the version](https://www.jooq.org/download/support-matrix).
2. psql -c "create database example_app;" -U postgres
2. psql -c "create user example_user with password 's3cr3t';" -U postgres
3. psql -d example_app -U postgres -f droptools-example/src/main/resources/db/migration/20141010__init.sql
4. psql -c "grant all on database example_app to example_user;" -U postgres
5. psql -c "grant all on all tables in schema ex to example_user" -d example_app -U postgres


Building
--------

To build the code, you need to run `./gradlew droptools-example:generatejooq`. To do that you need to run through the Setup steps above.

Testing
-------

In the main directory run Mac/Linux `./gradlew droptools-example:run` or Windows `.\gradlew.bat droptools-example:run`.
Then navigating to, or using curl/wget `http://127.0.0.1:8080/posts`.