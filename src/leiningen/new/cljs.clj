(ns leiningen.new.cljs
  (:require [leiningen.new.common :refer :all]
            [clojure.string :refer [join]]))

(defn cljs-assets [features]
  (concat [["{{client-path}}/{{sanitized}}/core.cljs" "cljs/src/cljs/core.cljs"]
           ["{{client-test-path}}/{{sanitized}}/core_test.cljs" "cljs/test/cljs/core_test.cljs"]
           ["{{resource-path}}/html/home.html" "cljs/resources/html/home.html"]
           ["{{resource-path}}/html/error.html" "core/resources/html/error.html"]
           ["env/dev/cljs/{{sanitized}}/app.cljs" "cljs/env/dev/cljs/app.cljs"]
           ["env/prod/cljs/{{sanitized}}/app.cljs" "cljs/env/prod/cljs/app.cljs"]]
          (when (some #{"+expanded"} features)
            [["{{cljc-path}}/{{sanitized}}/validation.cljc" "cljs/src/cljc/validation.cljc"]])))

(def cljs-version "1.10.879")

(def doo-version "0.1.11")

(def cljs-dependencies
  [['org.clojure/clojurescript cljs-version :scope "provided"]
   ['com.cognitect/transit-clj "1.0.324"]
   ['com.cognitect/transit-cljs "0.8.269"]])

;;NOTE: under boot, src/cljs is also added to source-paths (see boot-cljs-features)

(def resource-paths
  ["target/cljsbuild"])

(defn clean-targets [features]
  (if (some #{"+shadow-cljs"} features)
    [:target-path "target/cljsbuild"]
    [:target-path
     [:cljsbuild :builds :app :compiler :output-dir]
     [:cljsbuild :builds :app :compiler :output-to]]))

(defn cljs-dev-dependencies [features]
  (let [piggieback-version "0.5.2"
        devtools-version "1.0.3"]
    (if (some #{"+shadow-cljs"} features)
    [['binaryage/devtools devtools-version]
     ['cider/piggieback piggieback-version]]
    [['doo doo-version]
     ['binaryage/devtools devtools-version]
     ['cider/piggieback piggieback-version]])))

(defn get-output-dir [features]
  (if (some #{"+boot"} features)
    ""
    "target/cljsbuild/"))

(defn dev-cljsbuild [{:keys [project-ns features client-path cljc-path]}]
  {:builds
   {:app
    {:source-paths [client-path cljc-path "env/dev/cljs"]
     :figwheel {:on-jsload (str project-ns ".core/mount-components")}
     :compiler
                   (merge
                     {:main          (str project-ns ".app")
                      :asset-path    "/js/out"
                      :output-to     (str (get-output-dir features) "public/js/app.js")
                      :output-dir    (str (get-output-dir features) "public/js/out")
                      :source-map    true
                      :optimizations :none
                      :pretty-print  true}
                     (when (some #{"+re-frame"} features)
                       {:closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                        :preloads ['re-frisk.preload #_'day8.re-frame-10x.preload]}))}}})

(defn uberjar-cljsbuild [{:keys [features client-path cljc-path]}]
  {:builds
   {:min
    {:source-paths [cljc-path client-path "env/prod/cljs"]
     :compiler
                   (merge
                     {:output-dir (str (get-output-dir features) "public/js")
                      :output-to (str (get-output-dir features) "public/js/app.js")
                      :source-map (str (get-output-dir features) "public/js/app.js.map")
                      :optimizations :advanced
                      :pretty-print false
                      :infer-externs true
                      :closure-warnings
                      {:externs-validation :off :non-standard-jsdoc :off}}
                     (when (some #{"+reagent" "+re-frame"} features)
                       {:externs ["react/externs/react.js"]}))}}})

(defn test-cljsbuild [{:keys [project-ns client-path cljc-path client-test-path]}]
  {:builds
   {:test
    {:source-paths [cljc-path client-path client-test-path]
     :compiler
                   {:output-to     "target/test.js"
                    :main          (str project-ns ".doo-runner")
                    :optimizations :whitespace
                    :pretty-print  true}}}})

(defn cljs-lein-features [[assets options :as state]]
  [assets
   (-> options
       (assoc
         :cljs-test {:build "test"})
       (append-options :source-paths [(:client-path options) (:cljc-path options)])
       (append-options :resource-paths resource-paths))])

;; Options for boot

;; TODO: review shadow-cljs for boot later

(defn boot-cljs-assets [{:keys [client-path]}]
  [[(str client-path "/app.cljs.edn") "cljs/src/cljs/app.cljs.edn"]])

(def cljs-boot-plugins '[[adzerk/boot-cljs "2.1.5" :scope "test"]
                         [crisptrutski/boot-cljs-test "0.3.4" :scope "test"]
                         [adzerk/boot-cljs-repl "0.4.0" :scope "test"]])

(def cljs-boot-dev-plugins
  '[[crisptrutski/boot-cljs-test "0.3.4" :scope "test"]
    [powerlaces/boot-figreload "0.1.1-SNAPSHOT" :scope "test"]
    [com.cemerick/piggieback "0.5.2" :scope "test"]
    [org.clojure/clojurescript cljs-version :scope "test"]
    [weasel "0.7.0" :scope "test"]
    [org.clojure/tools.nrepl "0.2.12" :scope "test"]])

(defn dev-cljs [options]
  (let [lein-map (dev-cljsbuild options)
        test-map (test-cljsbuild options)]
    (merge
     {:source-paths (join " " (map #(str "\"" % "\"")
                                   (get-in lein-map [:builds :app :source-paths])))
      :compiler     (get-in lein-map [:builds :app :compiler])
      :test         {:source-paths (get-in test-map [:builds :test :source-paths])
                     :compiler     (get-in test-map [:builds :test :compiler])}}
     (when-not (some #{"+shadow-cljs"} (:features options))
       {:figwheel (get-in lein-map [:builds :app :figwheel])}))))

(defn cljs-boot-features [[assets options :as state]]
  [(into assets (boot-cljs-assets options))
   (-> options
       (append-options :dependencies cljs-boot-plugins)
       (append-options :dev-dependencies cljs-boot-dev-plugins)
       (append-options :source-paths (conj [(:cljc-path options)] (:client-path options)))
       (assoc :dev-cljs (dev-cljs options)))])

(defn cljs-features [[assets options :as state]]
  (let [features (:features options)]
    (if (some #{"+cljs"} features)
      (let [updated-state
            [(into (remove-conflicting-assets assets ".html") (cljs-assets features))
             (-> options
                 (append-options :dependencies cljs-dependencies)
                 (append-options :dev-dependencies (cljs-dev-dependencies features))
                 (update-in [:clean-targets] (fnil into []) (clean-targets features))
                 (assoc :cljs true))]
            boot? (some #{"+boot"} (:features options))]
        (if boot?
          (cljs-boot-features updated-state)
          (cljs-lein-features updated-state)))
      state)))
