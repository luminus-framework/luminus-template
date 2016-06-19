## 2.9.10.71

- bumped up Compojure

## 2.9.10.70

- added Dockerfile and Capstan configs
- bumped up reagent-utils to 0.1.9

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
