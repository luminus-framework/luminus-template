# Luminus-Template

A Leiningen template for projects using [Luminus](http://www.luminusweb.net/).

The template initializes a base Luminus application.

## Requirements

Luminus requires Leiningen version 2.5.3+

## Usage

The Luminus template ships out of the box with your latest Leiningen. Run the following
command to create a new Luminus project which uses the default profile template:

```bash
lein new luminus <your project name>
```

However, if you would like to attach further functionality to your template you can append [profile hints][ph] for these extended features:

### alternative servers

* `+aleph` adds the [Aleph](https://github.com/ztellman/aleph) server
* `+http-kit` adds the fast [HTTP Kit](http://www.http-kit.org/) web server to the project
* `+jetty` adds the [jetty](https://github.com/mpenet/jet) web server to the project

### databases

* `+h2` adds db.core namespace and [H2 database][h2] dependencies
* `+postgres` adds db.core namespace and add [PostreSQL database][pg] dependencies
* `+mysql` adds db.core namespace and add [MySQL/MariaDB database][my] dependencies
* `+mongodb` adds support for [MongoDB][mongo] using the [Monger][monger] library

### misc

* `+auth` adds [Buddy](https://github.com/funcool/buddy) dependency and authentication middleware
* `+cljs` adds [ClojureScript][cljs] support to the project along with an example
* `+cucumber` adds support for browser based UI testing with [Cucumber][cucumber] and [clj-webdriver][clj-webdriver]
* `+swagger` adds support for [Swagger-UI](https://github.com/swagger-api/swagger-ui) using the [compojure-api](https://github.com/metosin/compojure-api) library
* `+sassc` adds support for [SASS/SCSS](http://sass-lang.com/) files using [SassC](http://github.com/sass/sassc) command line compiler

To add a profile simply pass it as an argument after your application name, e.g.:

```bash
lein new luminus myapp +cljs
```

You can also mix multiple profiles when creating the application, e.g.:

```bash
lein new luminus myapp +auth +postgres
```

To build as a executable [Java ARchive (JAR)][jar] standalone, run the following command:

```bash
lein uberjar
```

To run the resulting standalone executable `.jar` file, do as you would with any other:

```bash
user$ java -jar target/myapp.jar
15-Sep-14 16:06:21 APc47d.4f39.65e6.uhn.ca INFO [myapp.handler] -
-=[myapp started successfully]=-
16:06:21.685 INFO  [org.projectodd.wunderboss.web.Web] (main) Registered web context /
15-Sep-14 16:06:21 APc47d.4f39.65e6.uhn.ca INFO [myapp.core] - server started on port: 3002
```

## Performance Testing

The app can be stress tested by running the [Apache benchmark](https://httpd.apache.org/docs/2.2/programs/ab.html) command:

```
ab -c 10 -n 1000 http://127.0.0.1:3000/
```

The memory and CPU usage can be inspected by running either `jconsole` or `jvisualvm` and attaching them to a running Luminus server.

## License

Copyright © 2015 Dmitri Sotnikov

Distributed under the [MIT License](http://opensource.org/licenses/MIT).

[ph]: <http://www.luminusweb.net/docs/profiles.md>
[tbs]: <http://twitter.github.io/bootstrap/>
[cljs]: <https://github.com/clojure/clojurescript>
[h2]: <http://www.h2database.com/html/main.html>
[pg]: <http://www.postgresql.org/>
[my]: <https://mariadb.org/>
[dc]: <https://www.dailycred.com/>
[kit]: <http://http-kit.org/>
[war]: <http://en.wikipedia.org/wiki/WAR_file_format_(Sun)>
[jar]: <http://en.wikipedia.org/wiki/Jar_file>
[cucumber]: <http://cukes.info>
[clj-webdriver]: <https://github.com/semperos/clj-webdriver>
[mongo]: <http://www.mongodb.com>
[monger]: <http://clojuremongodb.info>
