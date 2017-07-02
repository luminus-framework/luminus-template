## 2.9.11.67

- ClojureScript 1.9.671

## 2.9.11.66

fixed typo

## 2.9.11.64

- ClojureScript 1.9.660
- Reagent 0.7.0

## 2.9.11.63

- updated to only include org.webjars/webjars-locator-jboss-vfs when using `+war` flag
- metosin/muuntaja 0.3.1
- com.cemerick/piggieback 0.2.2

## 2.9.11.61

- org.clojure/tools.logging 0.4.0

## 2.9.11.60

- [PR 291](https://github.com/luminus-framework/luminus-template/pull/291) - :oauth key in options generated when using +oauth profile

## 2.9.11.59

- pjstadig/humane-test-output 0.8.2
- reagent 0.6.2
- re-frame 0.9.4

## 2.9.11.58

- ClojureScript 1.9.562
- metosin/ring-http-response 0.9.0
- ring-webjars 0.2.0
- ring/ring-core 1.6.1
- ring/ring-devel 1.6.1
- luminus-migrations 0.3.5

## 2.9.11.57

- luminus-migrations 0.3.4

## 2.9.11.56

- removed duplicate clj-time dependency

## 2.9.11.55

- conman 0.6.4
- lein-cprop 1.0.3

## 2.9.11.54

- ClojureScript 1.9.542
- cljs-ajax 0.6.0
- updated to use clj-time by default

## 2.9.11.53

- luminus-migrations 0.3.3
- migratus-lein 0.4.7

## 2.9.11.52

- ring-defaults 0.3.0

## 2.9.11.51

- luminus-migrations 0.3.2
- migratus-lein 0.4.6

## 2.9.11.50

- Compojure 1.6.0

## 2.9.11.49

- luminus-migrations 0.3.1
- migratus-lein 0.4.5

## 2.9.11.48

- Ring 1.6.0

## 2.9.11.47

- moved Figwheel reload config to project.clj
- binaryage/devtools 0.9.4
- markdown-clj 0.9.99

## 2.9.11.46

- Ring 1.6.0-RC3

## 2.9.11.45

- datomic-free 0.9.5561
- markdown-clj 0.9.99
- ClojureScript 1.9.521
- binaryage/devtools 0.9.3
- figwheel 0.5.10
## 2.9.11.44

- ClojureScript 1.9.518
- cljs-ajax 0.5.9

## 2.9.11.43

- added the HTTP server state when calling `(start)` and `(stop)` functions.

## 2.9.11.42

- com.jakemccrary/lein-test-refresh 0.19.0
- ring/ring-core 1.6.0-RC2

## 2.9.11.41

- luminus/ring-ttl-session 0.3.2
- metosin/muuntaja 0.2.1

## 2.9.11.40

- luminus/ring-ttl-session 0.3.2
- metosin/muuntaja 0.2.0

## 2.9.11.39

- monger com.novemberain/monger 3.1.0
- selmer 1.10.7
- switched from ring-middleware-format to metosin/muuntaja as the default

## 2.9.11.38

- ^:skip-aot for main to avoid unnecessary AOT
- binaryage/devtools 0.9.2
- metosin/ring-http-response 0.8.2
- org.postgresql/postgresql 42.0.0
- reagent-utils 0.2.1

## 2.9.11.37

- markdown-clj 0.9.98
- ring/ring-core 1.6.0-RC1

## 2.9.11.36

- reagent 0.6.1

## 2.9.11.35

- ClojureScript 1.9.495
- lein-cljsbuild 1.1.5
- switched from using bouncer to struct as the default validation library

## 2.9.11.35

- fix for the +cljs profile
- luminus-migrations 0.3.0
- markdown-clj 0.9.97
- migratus-lein 0.4.4

## 2.9.11.34

- use `:test-paths` for test sources

## 2.9.11.33

- markdown-clj 0.9.95
- re-frame 0.9.2

## 2.9.11.32

- ring-defaults 0.2.3

## 2.9.11.31

- markdown-clj 0.9.94
- lein-figwheel 0.5.9
- buddy 1.3.0
- h2 1.4.193
- mysql/mysql-connector-java 6.0.5
- org.xerial/sqlite-jdbc 3.16.1
- ring-middleware-format 0.7.2
- selmer 1.10.6

## 2.9.11.30

- fix for the selenium profile

## 2.9.11.29

- ClojureScript 1.9.456
- binaryage/devtools 0.9.0

## 2.9.11.28

- markdown-clj 0.9.92

## 2.9.11.27

- conman 0.6.3

## 2.9.11.26

- rolled back to org.webjars/bootstrap 4.0.0-alpha.5

## 2.9.11.25

- immutant 2.1.6
- compojure 1.5.2
- cprop 0.1.10
- metosin/compojure-api 1.1.10
- metosin/ring-http-response 0.8.1
- org.webjars.bower/tether 1.4.0
- org.webjars/bootstrap 4.0.0-alpha.6
- ring/ring-defaults 0.2.2
- ring/ring-devel 1.5.1

## 2.9.11.24

- added an explicit Ring dependency (1.5.1)

## 2.9.11.23

- selmer 1.10.5

## 2.9.11.22

- improved Hoplon profile

## 2.9.11.21

- added `+hoplon` profile

## 2.9.11.20

- lein-test-refresh 0.18.1
- luminus-migrations 0.2.9
- mount 0.1.11
- re-frame 0.9.1
- org.xerial/sqlite-jdbc 3.15.1

## 2.9.11.19

- selmer 1.10.3
- renamed `env/dev/cljs/dev.cljs to `env/dev/cljs/app.cljs`
- renamed `env/prod/cljs/prod.cljs to `env/prod/cljs/app.cljs`
- updated `+cljs` profile to remove unused externs, and fixed the `init!` function to work in uberjar

## 2.9.11.18

- re-frame 0.9.0

## 2.9.11.17

- Selmer 1.10.2
- removed `*identity*` key from the `+auth` profile, the identity in the session should be used instead

The HTTP server is no longer stopped and started by the `(start)`/`(stop)` helpers in the REPL since it's marked as `^{:on-reload :noop}`, this does not work with `wrap-reload` middleware.

## 2.9.11.16

- rollback to re-frame 0.8.0

## 2.9.11.15

- Selmer 1.10.1
- re-frame 0.9.0

## 2.9.11.14

- binaryage/devtools 0.8.3
- buddy 1.2.0
- cider/cider-nrepl 0.15.0-SNAPSHOT
- markdown-clj 0.9.91
- org.webjars/font-awesome 4.7.0
- prone 1.1.4


## 2.9.11.13

- fixed typo in fetching docs for the reagent/re-frame templates

## 2.9.11.12

- org.postgresql/postgresql 9.4.1212
- luminus-migrations 0.2.9

## 2.9.11.11

- split out dev/test resources
- metosin/compojure-api 1.1.9
- org.webjars/bootstrap 4.0.0-alpha.5

## 2.9.11.10

- ClojureScript 1.9.293

## 2.9.11.09

- consolidated docs

## 2.9.11.08

- improved documentation in the generated template

## 2.9.11.07

- updated the template page for new projects

## 2.9.11.06

- bouncer 1.1.0
- Selmer 1.10.0
- markdown-clj 0.9.90
- jquery 3.1.1
- luminus-migrations 0.8.32
- dependencies are now sorted alphabetically

## 2.9.11.05

- split up cljs/reagent profiles

## 2.9.11.04

- added `:project/dev` to `:test` profile
- org.webjars.bower/tether 1.3.7
- org.postgresql/postgresql 9.4.1211
- figwheel 0.5.8

## 2.9.11.03

- +oauth profile
- fixed typo in the datomic profile

## 2.9.11.02

- updated to only set default headers for local URIs in cljs-ajax
- removed Accept header as cljs-ajax [default behavior](https://github.com/JulianBirch/cljs-ajax/issues/130) was fixed

## 2.9.11.01

- luminus-migrations 0.2.7
- migratus-lein 0.8.31

## 2.9.11.00

- conman 0.6.1

## 2.9.10.99

- luminus-aleph 0.1.5
- binaryage/devtools 0.8.2
- added log suppression org.apache.http by default
- selmer 1.0.9

## 2.9.10.98

- Reagent 0.6.0
- prone 1.1.2

## 2.9.10.97

 - clj-time 0.12.0 for the +atuh-jwe profile

## 2.9.10.96

 - +auth-jwe profile
 - datomic 0.9.5394
 - compojure-api 1.1.8
 - clojurescript 1.9.229
 - buddy 1.1.0
 - figwheel-sidecar 0.5.7

## 2.9.10.95

- lein-cljsbuild 1.1.4

## 2.9.10.94

- added compojure-api authentication boilerplate for the +auth profile

## 2.9.10.93

- ClojureScript 1.9.225
- re-frame 0.8.0
- cprop 0.1.9

## 2.9.10.92

added `+re-frame` profile

## 2.9.10.91

- ClojureScript 1.9.211

## 2.9.10.90

- ClojureScript 1.9.198

## 2.9.10.89

- added content type for docs

## 2.9.10.88

- Cider middleware in Figwheel config when using `+cider`
- ClojureScript 1.9.183
- humane-test-output 0.8.1

## 2.9.10.87

- conman 0.6.0
- devtools 0.8.1
- bootstrap 4.0.0-alpha.3
- compojure-api 1.1.6

## 2.9.10.86

- updated to conman 0.5.9

## 2.9.10.85

- fixed default route order for the `+swagger` profile

## 2.9.10.84

- moved cider to dependencies

## 2.9.10.83

- reagent-utils 0.2.0
- Cider profile
- binaryage/devtools 0.8.0

## 2.9.10.82

- migratus 0.8.26
- luminus-migrations 0.2.6
- Postgres JDBC driver 9.4.1209
- jQuery 3.0.0
- compojure-api 1.1.5

## 2.9.10.81

- removed source maps from default uberjar config

## 2.9.10.80

- split cljsbuild into separate profiles
- lein-doo 0.1.7
- tether 1.3.3

## 2.9.10.79

- http-kit 2.2.0
- lein-doo 0.1.7
- compojure-api 1.1.4

## 2.9.10.78

- switched to use logback
- bumped up luminus-migrations to 0.2.5
- bumped up Figwheel to 0.5.4-7

## 2.9.10.77

- switched to log4j2
- updated to latest Immutant

## 2.9.10.76

- updated migratus-lein
- updated log4j
- removed default config in db config, uses hikari-cp defaults instead

## 2.9.10.75

- updated migratus/luminus migrations
- updated cljs/figwheel
- updated Selmer
- updated metosin/ring-http-response

## 2.9.10.74

- ClojureScript to 1.9.92
- Figwheel to 0.5.4.-4

## 2.9.10.73

- Selmer to 1.0.6
- cljs-ajax to 0.5.8
- ClojureScript to 1.9.88
- binaryage/devtools 0.7.2
- metosin/ring-http-response 0.7.0
- metosin/compojure-api 1.1.3

## 2.9.10.72

- bumped up cljs-ajax to 0.5.7
- bumped up ClojureScript to 1.9.76

## 2.9.10.71

- bumped up Compojure
- bumped down ClojureScript to 1.9.36 due to [transit-cljs warning](https://github.com/cognitect/transit-cljs/issues/26)

## 2.9.10.70

- added Dockerfile and Capstan configs
- bumped up reagent-utils to 0.1.9
- bumped up ClojureScript to 1.9.76
- bumped up cljs-ajax to 0.5.6

## 2.9.10.69

- fix for JSON serialization in Postgres
- updated to Figwheel 0.5.4-3

## 2.9.10.68

- fixed tests generated for the db profile to match latest luminus-migrations

## 2.9.10.67

- cleaned up figwheel middleware
- bumped up migratus/luminus-migrations

## 2.9.10.66

- bumped up Figwheel to 0.5.4-2
- bumped up cljs-dev-tools to 0.7.0

## 2.9.10.65

- bumped up luminus-migrations, now supports an options map same as migratus
- bumped up Figwheel to 0.5.4-1
- cleaned up dev deps/plugins

## 2.9.10.64

- rolled back Figwheel, snapshot versions can be unstable

## 2.9.10.63

- updated to Reagent 0.6.0-rc

## 2.9.10.62

- updated ring-defaults/ring-devel
- updated migratus

## 2.9.10.61

- added [lein-immutant](https://github.com/immutant/lein-immutant) as the default plugin for the Immutant profile,
  this allows seamless deployment to Wildfly and running as standalone

## 2.9.10.60

- bumped up cljs to 1.9.36
- fix for regression in 2.9.10.59 for the `+service` profile

## 2.9.10.59

- fix for `wrap-webjars` mime type inference

## 2.9.10.58

- moved figwheel config to top level

## 2.9.10.57

- added cljs-devtools support

## 2.9.10.56

- removed cljs profiles

## 2.9.10.55

- bumped up luminus-migrations
- bumped up Aleph, fix for Aleph to join when running in uberjar

## 2.9.10.54

- bumped up figwheel, cljs-ajax, compojure-api


## 2.9.10.53

- updated conman verison
- `+war` profile tests fix
- `+datomic` profile for Datomic support

## 2.9.10.51

- fix for hikari-cp usage in conman

## 2.9.10.51

- fix for conman

## 2.9.10.50

- bumped up to latest clojure.java.jdbc
- bumped up alpeh/jetty dependencies
- switched logger to use `defstate`

## 2.9.10.49

- bumped up migratus, conman

## 2.9.10.48

- updated migratus/conman

## 2.9.10.47

- bumped up Reagent, clojure.tools.cli

## 2.9.10.46

- bumped up dependencies

## 2.9.10.45

- bumped up markdown-clj
- fixed `+war` profile

## 2.9.10.44

- bumped up migratus

## 2.9.10.43

- updated init/stop initialization states to use `defstate`
- switched to latest clojure.java.jdbc/HugSQL

## 2.9.10.42

- fixed default tests to match the new template

## 2.9.10.41

- rolled back conman to v0.4.9

## 2.9.10.40

- fixes for +service profile

## 2.9.10.38

- removed wrap-formats from global middleware
- added +service profile
- updated font-awesome
- simplified Swagger example code

## 2.9.10.37

- bumped up conman, reagent-utils versions

## 2.9.10.36

- switched `app` to be a function to ensure `env` is available when middleware is loaded

## 2.9.10.35

- bumped up conman

## 2.9.10.34

- bumped up conman, markdown-clj versions

## 2.9.10.33

- removed Leiningen dependency for running ClojureScript REPL

## 2.9.10.32

- removed unused dev puget dependency

## 2.9.10.31

- switched to luminus/ring-ttl-session for non-immutant HTTP servers

## 2.9.10.30

- bumped ClojureScript to 1.8.40

## 2.9.10.29

- fix for target path in Heroku Procfile

## 2.9.10.28

- updated dependencies, latest `conman` release supports setting `doc` meta for generated functions

## 2.9.10.27

- ClojureScript cleanup

## 2.9.10.25

- added [lein-test-refresh](https://github.com/jakemcc/lein-test-refresh) to default dev plugins

## 2.9.10.24

- fixed bootstrap styling for navbar

## 2.9.10.23

- added `^{:on-reload :noop}` for HTTP/nREPL server states

## 2.9.10.21

- fixed regression with loading exteranl logging configuration

## 2.9.10.19

- enables the Figwheel REPL via this [PR](https://github.com/luminus-framework/luminus-template/pull/213), it's now possible to start and stop the figwheel compiler from the `user` namespace by running `start-fw` and `stop-fw` respectively. The ClojureScript REPL is start by running `cljs`.

## 2.9.10.18

- fixed a regression in the `+war` profile

## 2.9.10.16

- added a default validation `cljc` namespace

## 2.9.10.15

- switched to use [profile-dependent target path to avoid cross-profile contamination](https://github.com/luminus-framework/luminus-template/pull/210)

## 2.9.10.6

- switched to using [cprop](https://github.com/tolitius/cprop) as the default configuration system
- updated to use [mount](https://github.com/tolitius/mount) for manging HTTP/nREPL server lifecycle
- removed init/destroy functions from handler unless using +war profile
- war profile defaults to putting the core namespace in the dev source path
- war profile sets up mock JNDI in the core namespace
- war profile puts HTTP server dependency under the dev profile

## 2.9.9.7

- switched from using Yesql to [HugSQL](http://www.hugsql.org/), see documentation for syntactic differences for SQL queries
- as a result of moving to HugSQL, [conman](https://github.com/luminus-framework/conman) no longer requires an atom to hold the connection

## 2.9.9.2

- updated to use a single `src` source path. The sources are found under `src/clj`, `src/cljc`, and `src/cljs` respectively.
- updated to latest compojure-api, see [official changelog](https://github.com/metosin/compojure-api/blob/master/CHANGELOG.md) for the syntactic changes
- updated to use cljs-ajax interceptors to provide default `Accept` and `x-csrf-token` headers
- updated to automatically generate ClojureScript test scaffolding when the `+cljs` flag is used

## 2.9.9.0

- added font awesome to the template

## 2.9.8.99

- switched to Bootstrap 4
- factored out logging to a plugin, allows for future alternate logging implementations, e.g: Timbre

## 2.9.8.96

- default HTTP port for production is now found in `prod/env/config.edn`

## 2.9.8.94

- switched to using [luminus/config](https://github.com/luminus-framework/config) instead of environ
- dev profile now provides a `user` namespace

## 2.9.8.93

- switched to Clojure 1.8

## 2.9.8.92

- split out log config to dev/prod profiles

## 2.9.8.85

- moved out HTTP/nREPL servers into plugins

## 2.9.8.79

- switched to using log4j as the default logger
