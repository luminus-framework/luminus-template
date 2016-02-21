(ns leiningen.new.cljs
  (:require [leiningen.new.common :refer :all]))

(def cljs-assets
  [["src/cljs/{{sanitized}}/core.cljs" "cljs/src/cljs/core.cljs"]
   ["src/cljs/{{sanitized}}/ajax.cljs" "cljs/src/cljs/ajax.cljs"]
   "src/cljc/{{sanitized}}"
   ["test/cljs/{{sanitized}}/doo_runner.cljs" "cljs/test/cljs/doo_runner.cljs"]
   ["test/cljs/{{sanitized}}/core_test.cljs" "cljs/test/cljs/core_test.cljs"]
   ["env/dev/cljs/{{sanitized}}/dev.cljs" "cljs/env/dev/cljs/app.cljs"]
   ["env/prod/cljs/{{sanitized}}/prod.cljs" "cljs/env/prod/cljs/app.cljs"]
   ["resources/templates/home.html" "cljs/templates/home.html"]
   ["resources/templates/error.html" "core/resources/templates/error.html"]])

(def cljs-dependencies
  [['org.clojure/clojurescript "1.7.228" :scope "provided"]
   ['reagent "0.5.1"]
   ['reagent-forms "0.5.20"]
   ['reagent-utils "0.1.7"]
   ['secretary "1.2.3"]
   ['org.clojure/core.async "0.2.374"]
   ['cljs-ajax "0.5.3"]])

(def source-paths
  ["src/cljc"])

(def resource-paths
  ["target/cljsbuild"])

(def cljs-plugins
  [['lein-cljsbuild "1.1.1"]])

(def cljs-dev-plugins
  [['lein-figwheel "0.5.0-6"]
   ['lein-doo "0.1.6"]
   ['org.clojure/clojurescript "1.7.228"]])

(def clean-targets [:target-path
                    [:cljsbuild :builds :app :compiler :output-dir]
                    [:cljsbuild :builds :app :compiler :output-to]])

(def cljs-dev-dependencies
  [['lein-figwheel "0.5.0-6"]
   ['lein-doo "0.1.6"]
   ['com.cemerick/piggieback "0.2.2-SNAPSHOT"]])

(def cljs-build
  {:builds {:app {:source-paths ["src/cljc" "src/cljs"]
                  :compiler     {:output-to    "target/cljsbuild/public/js/app.js"
                                 :output-dir   "target/cljsbuild/public/js/out"
                                 :externs      ["react/externs/react.js"]
                                 :pretty-print true}}}})

(def cljs-uberjar
  {:prep-tasks ["compile" ["cljsbuild" "once"]]
   :cljsbuild  {:builds {:app
                         {:source-paths ["env/prod/cljs"]
                          :compiler     {:optimizations :advanced
                                         :pretty-print  false
                                         :closure-warnings
                                                        {:externs-validation :off
                                                         :non-standard-jsdoc :off}}}}}})

(defn cljs-dev [{:keys [project-ns]}]
  {:cljsbuild {:builds
               {:app
                {:source-paths ["env/dev/cljs"]
                 :compiler     {:main       (str project-ns ".app")
                                :asset-path "/js/out"
                                :optimizations :none
                                :source-map true}}
                :test {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                       :compiler {:output-to "target/test.js"
                                  :main (str project-ns ".doo-runner")
                                  :optimizations :whitespace
                                  :pretty-print true}}}}})

(def cljs-test
  {:build "test"})

(defn figwheel [{:keys [project-ns]}]
  {:http-server-root "public"
   :server-port      3449
   :nrepl-port       7002
   :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
   :css-dirs         ["resources/public/css"]
   :ring-handler     (symbol (str project-ns ".handler/app"))})

(defn cljs-features [[assets options :as state]]
  (if (some #{"+cljs"} (:features options))
    [(into (remove-conflicting-assets assets ".html") cljs-assets)
     (-> options
         (append-options :dependencies cljs-dependencies)
         (append-options :plugins cljs-plugins)
         (append-options :source-paths source-paths)
         (append-options :resource-paths resource-paths)
         (append-options :dev-dependencies cljs-dev-dependencies)
         (append-options :dev-plugins cljs-dev-plugins)
         (update-in [:clean-targets] (fnil into []) clean-targets)
         (assoc
           :cljs true
           :cljs-build (indent root-indent cljs-build)
           :cljs-test cljs-test
           :figwheel (indent dev-indent (figwheel options))
           :cljs-dev (unwrap-map (indent dev-indent (cljs-dev options)))
           :cljs-uberjar (unwrap-map (indent uberjar-indent cljs-uberjar))))]
    state))
