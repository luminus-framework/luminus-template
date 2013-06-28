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

* `+bootstrap` - adds [Twitter Bootstrap][tbs] CSS/JS static resources
* `+cljs` - adds [ClojureScript][cljs] support to the project along with an example
* `+h2` - adds models.db namespace and [H2 database][h2] dependencies
* `+postgres` - adds models.db namespace and add [PostreSQL database][pg] dependencies
* `+mysql` - adds models.db namespace and add [MySQL/MariaDB database][my] dependencies
* `+site` - registration and authentication setup, uses `+bootstrap` and `+h2` when
  database is not specified
* `+dailycred` - add support for [DailyCred][dc], used with `+site` it configures the
  application to authenticate with DailyCred
* `+http-kit` - adds [HTTP Kit][kit] support to the project



To add a profile simply pass it as an argument after your application name, e.g.:

```bash
lein new luminus myapp +bootstrap
```

You can also mix multiple profiles when creating the application, e.g.:

```bash
lein new luminus myapp +site +postgres
```

To build as a executable [Java ARchive (JAR)][jar] standalone, run the following command:

```bash
lein ring uberjar
```

You'll be able to run the 

To run the resulting standalone executable `.jar` file, do as you would with any other:

```bash
java -jar target/myapp-0.1.0-SNAPSHOT-standalone.jar

2012-12-15 19:17:23.471:INFO:oejs.Server:jetty-7.x.y-SNAPSHOT
2012-12-15 19:17:23.512:INFO:oejs.AbstractConnector:Started
SelectChannelConnector@0.0.0.0:8080
Server started on port [ 8080 ].
You can view the site at http://localhost:8080
```

To build a [WAR][war] (or Web application ARchive) file run:

```bash
lein ring uberwar
```

You can then easily deploy the resulting WAR to Tomcat or any other Java application
server.

## License

Copyright © 2012 Yogthos

Distributed under the Eclipse Public License, the same as Clojure.

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
