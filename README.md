# Luminus-Template

A Leiningen template for projects using [Luminus](http://www.luminusweb.net/).

The template initializes a base Luminus application.

## Requirements

Luminus requires Leiningen version 2.x

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
* `+immutant` adds the [Immutant](http://immutant.org/) web server to the project

### databases

* `+h2` adds db.core namespace and [H2 database][h2] dependencies
* `+postgres` adds db.core namespace and add [PostreSQL database][pg] dependencies
* `+mysql` adds db.core namespace and add [MySQL/MariaDB database][my] dependencies
* `+mongodb` adds support for [MongoDB][mongo] using the [Monger][monger] library

### misc

* `+auth` adds [Buddy](https://funcool.github.io/buddy/latest/) dependency and authentication middleware
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
lein ring uberjar
```

To run the resulting standalone executable `.jar` file, do as you would with any other:

```bash
user$ java -jar target/myapp-0.1.0-SNAPSHOT-standalone.jar

2012-12-15 19:17:23.471:INFO:oejs.Server:jetty-7.x.y-SNAPSHOT
2012-12-15 19:17:23.512:INFO:oejs.AbstractConnector:Started
SelectChannelConnector@0.0.0.0:3000
Server started on port [ 3000 ].
You can view the site at http://localhost:3000
```

To build a [WAR][war] (or Web application ARchive) file run:

```bash
lein ring uberwar
```

You can then easily deploy the resulting WAR to Tomcat or any other Java application
server.

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
