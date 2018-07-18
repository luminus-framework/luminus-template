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

### routing

* `+reitit` adds [Reitit](https://metosin.github.io/reitit/) Clojure/Script router support

### alternative servers

* `+aleph` adds the [Aleph](https://github.com/ztellman/aleph) server
* `+http-kit` adds the fast [HTTP Kit](http://www.http-kit.org/) web server to the project
* `+jetty` adds the [jetty](https://github.com/mpenet/jet) web server to the project

### databases

* `+h2` adds db.core namespace and [H2 database][h2] dependencies
* `+postgres` adds db.core namespace and add [PostreSQL database][pg] dependencies
* `+mysql` adds db.core namespace and add [MySQL/MariaDB database][my] dependencies
* `+mongodb` adds support for [MongoDB][mongo] using the [Monger][monger] library
* `+datomic` adds support for the [Datomic](http://www.datomic.com/) database

### service API

* `+graphql` - adds GraphQL support using [Lacinia](https://github.com/walmartlabs/lacinia)
* `+swagger` adds support for [Swagger-UI](https://github.com/swagger-api/swagger-ui) using the [compojure-api](https://github.com/metosin/compojure-api) library
* `+service` removes static assets and the layout, adds Swagger support

### ClojureScript

* `+cljs` adds [ClojureScript][cljs] support to the project
* `+reagent` adds [ClojureScript][cljs] support with [Reagent](https://reagent-project.github.io/) to the project along with an example
* `+re-frame` adds [ClojureScript][cljs] support with [re-frame](https://github.com/Day8/re-frame) to the project along with an example
* `+kee-frame` added [kee-frame](https://github.com/ingesolvoll/kee-frame) to the project

### misc

* `+boot` causes the project to run with [Boot](https://github.com/boot-clj/boot) instead of [Leiningen](https://github.com/technomancy/leiningen/)
* `+auth` adds [Buddy](https://github.com/funcool/buddy) dependency and authentication middleware
* `+auth-jwe` adds [Buddy](https://github.com/funcool/buddy) dependency with the [JWE](https://jwcrypto.readthedocs.io/en/stable/jwe.html) backend
* `+oauth` adds [OAuth](https://github.com/mattrepl/clj-oauth) dependency
* `+hoplon` adds [ClojureScript][cljs] support with [Hoplon](https://github.com/hoplon/hoplon) to the project
* `+cucumber` adds support for browser based UI testing with [Cucumber][cucumber] and [clj-webdriver][clj-webdriver]
* `+sassc` adds support for [SASS/SCSS](http://sass-lang.com/) files using [SassC](http://github.com/sass/sassc) command line compiler
* `+war` adds support of building WAR archives for deployment to servers such as Apache Tomcat (should NOT be used for [Immutant apps running on WildFly][immutant])
* `+site` creates template for site using the specified database (H2 by default) and ClojureScript
*  `+kibit` add [lein-kibit](https://github.com/jonase/kibit) plugin
* `+servlet` adds middleware for handling Servlet context

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
Or if using the +boot profile:
```bash
boot uberjar
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

## Other Templates

* [chestnut](https://github.com/plexus/chestnut)
* [duct](https://github.com/duct-framework/duct)
* [fulcro](https://github.com/fulcrologic/fulcro)
* [pedestal](https://github.com/pedestal/pedestal)
* [reagent-template](https://github.com/reagent-project/reagent-template)
* [re-frame-template](https://github.com/Day8/re-frame-template)
* [reagent-figwheel](https://github.com/gadfly361/reagent-figwheel)
* [reagent-seed](https://github.com/gadfly361/reagent-seed)
* [untangled](https://github.com/untangled-web/untangled-template)
* [vase](https://github.com/cognitect-labs/vase)


There is also a public [comparison chart](https://goo.gl/ZZH8fm) of the common templates.

## License

Copyright © 2016 Dmitri Sotnikov

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
[immutant]: <http://www.luminusweb.net/docs/deployment.md#deploying_to_wildfly>
