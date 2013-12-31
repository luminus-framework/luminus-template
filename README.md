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

* `+cljs` adds [ClojureScript][cljs] support to the project along with an example
* `+h2` adds models.db namespace and [H2 database][h2] dependencies
* `+postgres` adds models.db namespace and add [PostreSQL database][pg] dependencies
* `+mysql` adds models.db namespace and add [MySQL/MariaDB database][my] dependencies
* `+site` registration/authentication, uses `+h2` by default
* `+dailycred` combined with `+site` it uses [DailyCred][dc] to authenticate
* `+http-kit` - adds the fast [HTTP Kit][kit] web server to the project

To add a profile simply pass it as an argument after your application name, e.g.:

```bash
lein new luminus myapp +cljs
```

You can also mix multiple profiles when creating the application, e.g.:

```bash
lein new luminus myapp +site +postgres
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

Copyright © 2012 Yogthos

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
