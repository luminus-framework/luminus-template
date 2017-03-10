(ns leiningen.new.cljs
  (:require [leiningen.new.common :refer :all]))

(def cljs-assets
  [["src/cljs/{{sanitized}}/core.cljs" "cljs/src/cljs/core.cljs"]
   ["src/cljc/{{sanitized}}/validation.cljc" "cljs/src/cljc/validation.cljc"]
   ["test/cljs/{{sanitized}}/doo_runner.cljs" "cljs/test/cljs/doo_runner.cljs"]
   ["test/cljs/{{sanitized}}/core_test.cljs" "cljs/test/cljs/core_test.cljs"]
   ["env/dev/cljs/{{sanitized}}/app.cljs" "cljs/env/dev/cljs/app.cljs"]
   ["env/dev/clj/{{sanitized}}/figwheel.clj" "cljs/env/dev/clj/figwheel.clj"]
   ["env/prod/cljs/{{sanitized}}/app.cljs" "cljs/env/prod/cljs/app.cljs"]
   ["resources/templates/home.html" "cljs/templates/home.html"]
   ["resources/templates/error.html" "core/resources/templates/error.html"]])

(def cljs-version "1.9.495")

(def figwheel-version "0.5.9")

(def cljs-dependencies
  [['org.clojure/clojurescript cljs-version :scope "provided"]])

(def source-paths
  ["src/cljc"])

(def resource-paths
  ["target/cljsbuild"])

(def cljs-plugins
  [['lein-cljsbuild "1.1.5"]])

(def cljs-dev-plugins
  [['lein-doo "0.1.7"]
   ['lein-figwheel figwheel-version]
   ['org.clojure/clojurescript cljs-version]])

(def clean-targets [:target-path
                    [:cljsbuild :builds :app :compiler :output-dir]
                    [:cljsbuild :builds :app :compiler :output-to]])

(def cljs-dev-dependencies
  [['doo "0.1.7"]
   ['binaryage/devtools "0.9.0"]
   ['figwheel-sidecar figwheel-version]
   ['com.cemerick/piggieback "0.2.2-SNAPSHOT"]])

(defn uberjar-cljsbuild [features]
  {:builds
   {:min
    {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
     :compiler
     (merge
       {:output-to "target/cljsbuild/public/js/app.js"
       :optimizations :advanced
       :pretty-print false
       :closure-warnings
       {:externs-validation :off :non-standard-jsdoc :off}}
       (when (some #{"+reagent" "+re-frame"} features)
         {:externs ["react/externs/react.js"]}))}}})

(defn dev-cljsbuild [{:keys [project-ns]}]
  {:builds
   {:app
    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
     :compiler
     {:main          (str project-ns ".app")
      :asset-path    "/js/out"
      :output-to     "target/cljsbuild/public/js/app.js"
      :output-dir    "target/cljsbuild/public/js/out"
      :source-map    true
      :optimizations :none
      :pretty-print  true}}}})

(defn test-cljsbuild [{:keys [project-ns]}]
  {:builds
   {:test
    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
     :compiler
     {:output-to     "target/test.js"
      :main          (str project-ns ".doo-runner")
      :optimizations :whitespace
      :pretty-print  true}}}})

(def cljs-test
  {:build "test"})

(defn figwheel [{:keys [features]}]
  (let [cider? (some #{"+cider"} features)]
    {:http-server-root "public"
     :nrepl-port       7002
     :css-dirs         ["resources/public/css"]
     :nrepl-middleware `[cemerick.piggieback/wrap-cljs-repl
                         ~@(when cider? ['cider.nrepl/cider-middleware])]}))

(defn cljs-features [[assets options :as state]]
  (if (some #{"+cljs"} (:features options))
    [(into (remove-conflicting-assets assets ".html") cljs-assets)
     (-> options
         (update :dependencies
                 #(remove (fn [[artifact]]
                            (= artifact 'org.webjars/jquery)) %))
         (append-options :dependencies cljs-dependencies)
         (append-options :plugins cljs-plugins)
         (append-options :source-paths source-paths)
         (append-options :resource-paths resource-paths)
         (append-options :dev-dependencies cljs-dev-dependencies)
         (append-options :dev-plugins cljs-dev-plugins)
         (update-in [:clean-targets] (fnil into []) clean-targets)
         (assoc
           :cljs true
           :dev-cljsbuild (indent dev-indent (dev-cljsbuild options))
           :test-cljsbuild (indent dev-indent (test-cljsbuild options))
           :uberjar-cljsbuild (indent uberjar-indent (uberjar-cljsbuild (:features options)))
           :cljs-test cljs-test
           :figwheel (indent root-indent (figwheel options))
           :cljs-uberjar-prep ":prep-tasks [\"compile\" [\"cljsbuild\" \"once\" \"min\"]]"))]
    state))
