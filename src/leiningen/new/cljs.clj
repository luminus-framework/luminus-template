(ns leiningen.new.cljs
  (:require [leiningen.new.common :refer :all]))

(def cljs-assets
  [["src/cljs/{{sanitized}}/core.cljs" "cljs/src/cljs/core.cljs"]
   ["src/cljs/{{sanitized}}/ajax.cljs" "cljs/src/cljs/ajax.cljs"]
   "src/cljc/{{sanitized}}"
   ["src/cljc/{{sanitized}}/validation.cljc" "cljs/src/cljc/validation.cljc"]
   ["test/cljs/{{sanitized}}/doo_runner.cljs" "cljs/test/cljs/doo_runner.cljs"]
   ["test/cljs/{{sanitized}}/core_test.cljs" "cljs/test/cljs/core_test.cljs"]
   ["env/dev/cljs/{{sanitized}}/dev.cljs" "cljs/env/dev/cljs/app.cljs"]
   ["env/dev/clj/{{sanitized}}/figwheel.clj" "cljs/env/dev/clj/figwheel.clj"]
   ["env/prod/cljs/{{sanitized}}/prod.cljs" "cljs/env/prod/cljs/app.cljs"]
   ["resources/templates/home.html" "cljs/templates/home.html"]
   ["resources/templates/error.html" "core/resources/templates/error.html"]])

(def cljs-version "1.9.36")

(def figwheel-version "0.5.4-SNAPSHOT")

(def cljs-dependencies
  [['org.clojure/clojurescript cljs-version :scope "provided"]
   ['reagent "0.6.0-alpha2"]
   ['reagent-utils "0.1.8"]
   ['secretary "1.2.3"]
   ['cljs-ajax "0.5.5"]])

(def source-paths
  ["src/cljc"])

(def resource-paths
  ["target/cljsbuild"])

(def cljs-plugins
  [['lein-cljsbuild "1.1.3"]])

(def cljs-dev-plugins
  [['lein-figwheel figwheel-version]
   ['lein-doo "0.1.6"]
   ['org.clojure/clojurescript cljs-version]])

(def clean-targets [:target-path
                    [:cljsbuild :builds :app :compiler :output-dir]
                    [:cljsbuild :builds :app :compiler :output-to]])

(def cljs-dev-dependencies
  [['lein-figwheel figwheel-version]
   ['lein-doo "0.1.6"]
   ['binaryage/devtools "0.6.1"]
   ['com.cemerick/piggieback "0.2.2-SNAPSHOT"]])

(defn cljs-builds [{:keys [project-ns]}]
  {:builds
   {:app
    {:source-paths ["src/cljc" "src/cljs" "env/dev/cljs"]
     :figwheel     true
     :compiler
                   {:main          (str project-ns ".app")
                    :asset-path    "/js/out"
                    :output-to     "target/cljsbuild/public/js/app.js"
                    :output-dir    "target/cljsbuild/public/js/out"
                    :optimizations :none
                    :source-map    true
                    :pretty-print  true}}
    :test
    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
     :compiler
                   {:output-to     "target/test.js"
                    :main          (str project-ns ".doo-runner")
                    :optimizations :whitespace
                    :pretty-print  true}}
    :min
    {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
     :compiler
                   {:output-to     "target/cljsbuild/public/js/app.js"
                    :output-dir    "target/uberjar"
                    :externs       ["react/externs/react.js"]
                    :optimizations :advanced
                    :pretty-print  false
                    :closure-warnings
                                   {:externs-validation :off :non-standard-jsdoc :off}}}}})

(def cljs-test
  {:build "test"})

(def figwheel
  {:http-server-root "public"
   :nrepl-port       7002
   :css-dirs         ["resources/public/css"]})

(defn cljs-features [[assets options :as state]]
  (if (some #{"+cljs"} (:features options))
    [(into (remove-conflicting-assets assets ".html") cljs-assets)
     (-> options
         (update :dependencies #(remove #{['org.webjars/jquery "2.2.2"]} %))
         (append-options :dependencies cljs-dependencies)
         (append-options :plugins cljs-plugins)
         (append-options :source-paths source-paths)
         (append-options :resource-paths resource-paths)
         (append-options :dev-dependencies cljs-dev-dependencies)
         (append-options :dev-plugins cljs-dev-plugins)
         (update-in [:clean-targets] (fnil into []) clean-targets)
         (assoc
           :cljs true
           :cljs-build (indent root-indent (cljs-builds options))
           :cljs-test cljs-test
           :figwheel (indent root-indent figwheel)
           :cljs-uberjar-prep ":prep-tasks [\"compile\" [\"cljsbuild\" \"once\" \"min\"]]"))]
    state))
