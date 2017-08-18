(ns leiningen.new.cljs
  (:require [leiningen.new.common :refer :all]
            [clojure.string :refer [join]]))

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

(def boot-cljs-assets
  [["src/app.cljs.edn" "cljs/src/app.cljs.edn"]])

(def cljs-version "1.9.908")

(def figwheel-version "0.5.13")

(def cljs-dependencies
  [['org.clojure/clojurescript cljs-version :scope "provided"]])

(def source-paths
  ["src/cljc"])
;;NOTE: under boot, src/cljs is also added to source-paths (see boot-cljs-features)

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
   ['binaryage/devtools "0.9.4"]
   ['figwheel-sidecar figwheel-version]
   ['com.cemerick/piggieback "0.2.2"]])

(defn get-output-dir [features]
  (if (some #{"+boot"} features)
    ""
    "target/cljsbuild/"))

(defn uberjar-cljsbuild [features]
  {:builds
   {:min
    {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
     :compiler
     (merge
       {:output-to (str (get-output-dir features) "public/js/app.js")
       :optimizations :advanced
       :pretty-print false
       :closure-warnings
       {:externs-validation :off :non-standard-jsdoc :off}}
       (when (some #{"+reagent" "+re-frame"} features)
         {:externs ["react/externs/react.js"]}))}}})

(defn dev-cljsbuild [{:keys [project-ns features]}]
  {:builds
   {:app
    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
     :figwheel {:on-jsload (str project-ns ".core/mount-components")}
     :compiler
     {:main          (str project-ns ".app")
      :asset-path    "/js/out"
      :output-to     (str (get-output-dir features) "public/js/app.js")
      :output-dir    (str (get-output-dir features) "public/js/out")
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

(defn cljs-lein-features [[assets options :as state]]
  [assets
   (-> options
       (assoc
        :dev-cljsbuild (indent dev-indent (dev-cljsbuild options))
        :test-cljsbuild (indent dev-indent (test-cljsbuild options))
        :uberjar-cljsbuild (indent uberjar-indent (uberjar-cljsbuild (:features options)))
        :cljs-test cljs-test
        :figwheel (indent root-indent (figwheel options))
        :cljs-uberjar-prep ":prep-tasks [\"compile\" [\"cljsbuild\" \"once\" \"min\"]]")
       (append-options :source-paths source-paths)
       (append-options :resource-paths resource-paths))])

;; Options for boot

(def boot-cljs-assets
  [["src/cljs/app.cljs.edn" "cljs/src/cljs/app.cljs.edn"]])

(def cljs-boot-plugins '[[adzerk/boot-cljs "2.1.0-SNAPSHOT" :scope "test"]
                         [crisptrutski/boot-cljs-test "0.3.2-SNAPSHOT" :scope "test"]
                         [adzerk/boot-cljs-repl "0.3.3" :scope "test"]])

(def cljs-boot-dev-plugins
  '[[crisptrutski/boot-cljs-test "0.3.2-SNAPSHOT" :scope "test"]
    [powerlaces/boot-figreload "0.1.1-SNAPSHOT" :scope "test"]
    [org.clojure/clojurescript cljs-version :scope "test"]
    [pandeiro/boot-http "0.7.6" :scope "test"]
    [weasel "0.7.0" :scope "test"]
    [org.clojure/tools.nrepl "0.2.12" :scope "test"]])

(defn dev-cljs [options]
  (let [lein-map (dev-cljsbuild options)
        test-map (test-cljsbuild options)]
    {:source-paths (join " " (map #(str "\"" % "\"")
                                  (get-in lein-map [:builds :app :source-paths])))
     :figwheel (get-in lein-map [:builds :app :figwheel])
     :compiler (get-in lein-map [:builds :app :compiler])
     :test {:source-paths (get-in test-map [:builds :test :source-paths])
            :compiler (get-in test-map [:builds :test :compiler])}}))

(defn cljs-boot-features [[assets options :as state]]
  [(into assets boot-cljs-assets)
   (-> options
       (append-options :dependencies cljs-boot-plugins)
       (append-options :dev-dependencies cljs-boot-dev-plugins)
       (append-options :source-paths (conj source-paths "src/cljs"))
       (assoc :dev-cljs (dev-cljs options)))])

(defn cljs-features [[assets options :as state]]
  (if (some #{"+cljs"} (:features options))
    (let [updated-state
          [(into (remove-conflicting-assets assets ".html") cljs-assets)
           (-> options
               (update :dependencies
                       #(remove (fn [[artifact]]
                                  (= artifact 'org.webjars/jquery)) %))
               (append-options :dependencies cljs-dependencies)
               (append-options :plugins cljs-plugins)
               (append-options :dev-dependencies cljs-dev-dependencies)
               (append-options :dev-plugins cljs-dev-plugins)
               (update-in [:clean-targets] (fnil into []) clean-targets)
               (assoc :cljs true))]
           boot? (some #{"+boot"} (:features options))]
      (if boot?
        (cljs-boot-features updated-state)
        (cljs-lein-features updated-state)))
      state))
