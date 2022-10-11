# Luminus-Template

[![Clojars Project](https://img.shields.io/clojars/v/luminus/lein-template.svg)](https://clojars.org/luminus/lein-template)

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



| Profile      | Category      | Description                                                                                                                                                     | Compare                                                                          |
| :---:        | :---:         | :---:                                                                                                                                                           | :---:                                                                            |
| +aleph       | server        | adds the [Aleph](https://github.com/ztellman/aleph) server                                                                                                      | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+aleph)       |
| +http-kit    | server        | adds the fast [HTTP Kit](https://github.com/http-kit/http-kit) web server to the project                                                                        | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+http-kit)    |
| +immutant    | server        | adds the [immutant](https://github.com/immutant/immutant) web server to the project. Note: this project is no longer funded/maintained                          | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+immutant)    |
| +jetty       | server        | adds the [jetty](https://github.com/luminus-framework/luminus-jetty) web server to the project                                                                  | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+jetty)       |
| +undertow    | server        | adds the [ring-undertow](https://github.com/luminus-framework/ring-undertow-adapter) server. **This is a default server.**                                      | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+undertow)    |
| +h2          | database      | adds db.core namespace and [H2 database][h2] dependencies                                                                                                       | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+h2)          |
| +postgres    | database      | adds db.core namespace and [PostgreSQL database][pg] dependencies                                                                                                | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+postgres)    |
| +mysql       | database      | adds db.core namespace and [MySQL/MariaDB database][my] dependencies                                                                                            | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+mysql)       |
| +mongodb     | database      | adds support for [MongoDB][mongo] using the [Monger][monger] library                                                                                            | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+mongodb)     |
| +datomic     | database      | adds support for the [Datomic](http://www.datomic.com/) database                                                                                                | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+datomic)     |
| +sqlite      | database      | adds support for the [SQLite](https://www.sqlite.org/) database                                                                                                 | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+sqlite)      |
| +xtdb        | database      | adds support for the [XTDB](https://xtdb.com/) database                                                                                                        | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+xtdb)      |
| +graphql     | service API   | adds GraphQL support using [Lacinia](https://github.com/walmartlabs/lacinia)                                                                                    | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+graphql)     |
| +swagger     | service API   | adds support for [Swagger-UI](https://github.com/swagger-api/swagger-ui)                                                                                        | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+swagger)     |
| +service     | service API   | removes static assets and the layout, adds Swagger support                                                                                                      | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+service)     |
| +cljs        | ClojureScript | adds [ClojureScript][cljs] support to the project                                                                                                               | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+cljs)        |
| +reagent     | ClojureScript | adds [ClojureScript][cljs] support with [Reagent](https://reagent-project.github.io/) to the project along with an example                                      | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+reagent)     |
| +re-frame    | ClojureScript | adds [ClojureScript][cljs] support with [re-frame](https://github.com/Day8/re-frame) to the project along with an example                                       | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+re-frame)    |
| +kee-frame   | ClojureScript | adds [kee-frame](https://github.com/ingesolvoll/kee-frame) to the project                                                                                       | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+kee-frame)   |
| +shadow-cljs | ClojureScript | adds [shadow-cljs](https://github.com/thheller/shadow-cljs) support to the project, replacing the default cljsbuild and figwheel setup                          | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+shadow-cljs) |
| +boot        | misc          | causes the project to run with [Boot](https://github.com/boot-clj/boot) instead of [Leiningen](https://github.com/technomancy/leiningen/)                       | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+boot)        |
| +auth        | misc          | adds [Buddy](https://github.com/funcool/buddy) dependency and authentication middleware                                                                         | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+auth)        |
| +auth-jwe    | misc          | adds [Buddy](https://github.com/funcool/buddy) dependency with the [JWE](https://jwcrypto.readthedocs.io/en/stable/jwe.html) backend                            | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+auth-jwe)    |
| +oauth       | misc          | adds [OAuth](https://github.com/mattrepl/clj-oauth) dependency                                                                                                  | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+oauth)       |
| +hoplon      | misc          | adds [ClojureScript][cljs] support with [Hoplon](https://github.com/hoplon/hoplon) to the project                                                               | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+hoplon)      |
| +cucumber    | misc          | adds support for browser based UI testing with [Cucumber][cucumber] and [clj-webdriver][clj-webdriver]                                                          | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+cucumber)    |
| +sassc       | misc          | adds support for [SASS/SCSS](http://sass-lang.com/) files using [SassC](http://github.com/sass/sassc) command line compiler                                     | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+sassc)       |
| +war         | misc          | adds support of building WAR archives for deployment to servers such as Apache Tomcat **(should NOT be used for [Immutant apps running on WildFly][immutant])** | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+war)         |
| +site        | misc          | creates template for site using the specified database (H2 by default) and ClojureScript                                                                        | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+site)        |
| +kibit       | misc          | adds [lein-kibit](https://github.com/jonase/kibit) plugin                                                                                                       | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+kibit)       |
| +servlet     | misc          | adds middleware for handling Servlet context                                                                                                                    | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+servlet)     |
| +basic       | misc          | generates a bare bones luminus project                                                                                                                          | [diff](https://github.com/nfedyashev/luminusdiff/compare/3.85..3.85+basic)       |
| +async       | misc          | support for async (= 3 argument) ring handlers                                                                                                                  |        |


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

## Async Ring Handlers

Using async ring handlers is possible but adds another layer of
complexity.  If things go wrong you'll see a blank screen, possibly
without any error message.

The server (undertow, jetty, servlet) and every middleware in the chain has to 
support async request handling.

To enable: add `:async? true` to your config maps.

Tested combinations:

* default
* +war
* +servlet +war (this implies jetty9): http://localhost:3000/your-ns instead of just /
* +jetty +war: http://localhost:3000/your-ns instead of just /
* +jetty: works
* +http-kit: the template works, but http-kit does not support async handlers
* +aleph: the template works, but aleph does not seem to support async handlers

## Other Templates

* [chestnut](https://github.com/plexus/chestnut)
* [duct](https://github.com/duct-framework/duct)
* [fulcro-template](https://github.com/fulcrologic/fulcro-template)
* [pedestal](https://github.com/pedestal/pedestal)
* [reagent-template](https://github.com/reagent-project/reagent-template)
* [re-frame-template](https://github.com/Day8/re-frame-template)
* [reagent-figwheel](https://github.com/gadfly361/reagent-figwheel)
* [reagent-seed](https://github.com/gadfly361/reagent-seed)
* [vase](https://github.com/cognitect-labs/vase)


There is also a public [comparison chart](https://goo.gl/ZZH8fm) of the common templates.

## License

Copyright © 2016 Dmitri Sotnikov

Distributed under the [MIT License](http://opensource.org/licenses/MIT).

[ph]: <http://www.luminusweb.net/docs/profiles.html>
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
