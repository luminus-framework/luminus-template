(ns leiningen.new.cljs
  (:require [leiningen.new.common :refer :all]))

(def cljs-assets
  [["src-cljs/<<sanitized>>/core.cljs" "cljs/src/cljs/core.cljs"]
   ["env/dev/clj/<<sanitized>>/dev.clj" "cljs/env/dev/clj/dev.clj"]
   ["env/dev/cljs/<<sanitized>>/dev.cljs" "cljs/env/dev/cljs/app.cljs"]
   ["env/prod/cljs/<<sanitized>>/prod.cljs" "cljs/env/prod/cljs/app.cljs"]
   ["resources/templates/home.html" "cljs/templates/home.html"]])

(def cljs-dependencies
  [['org.clojure/clojurescript "0.0-3058" :scope "provided"]
   ['reagent-forms "0.4.4"]
   ['reagent-utils "0.1.3"]
   ['secretary "1.2.1"]
   ['org.clojure/core.async "0.1.346.0-17112a-alpha"]
   ['cljs-ajax "0.3.10"]])

(def clean-targets ["resources/public/js"])

(def cljs-dev-dependencies
  [['leiningen "2.5.1"]
   ['figwheel "0.2.5-SNAPSHOT"]
   ['weasel "0.6.0-SNAPSHOT"]
   ['com.cemerick/piggieback "0.1.6-SNAPSHOT"]])

(def cljs-build
  {:builds {:app {:source-paths ["src-cljs"]
                  :compiler     {:output-to     "resources/public/js/app.js"
                                 :output-dir    "resources/public/js/out"
                                 :source-map    "resources/public/js/out.js.map"
                                 :externs       ["react/externs/react.js"]
                                 :optimizations :none
                                 :pretty-print  true}}}})

(def cljs-uberjar
  {:hooks     ['leiningen.cljsbuild]
   :cljsbuild {:jar    true
               :builds {:app
                        {:source-paths ["env/prod/cljs"]
                         :compiler
                                       {:optimizations :advanced
                                        :pretty-print  false}}}}})

(def cljs-dev
  {:cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]}}}})

(defn figwheel [{:keys [project-ns]}]
  {:http-server-root "public"
   :server-port 3449
   :css-dirs ["resources/public/css"]
   :ring-handler (symbol (str project-ns ".handler/app"))})

(defn cljs-features [[assets options :as state]]
  (if (some #{"+cljs"} (:features options))
    [(into (remove-conflicting-assets assets ".html") cljs-assets)
     (-> options
         (append-options :dependencies cljs-dependencies)
         (append-options :dev-dependencies cljs-dev-dependencies)
         (append-options :plugins [['lein-cljsbuild "1.0.4"]])
         (append-options :dev-plugins [['lein-figwheel "0.2.3-SNAPSHOT"]])
         (append-options :dev-source-paths ["env/dev/clj"])
         (update-in [:clean-targets] (fnil into []) clean-targets)
         (assoc
           :cljs-build (indent root-indent cljs-build)
           :figwheel (indent dev-indent (figwheel options))
           :cljs-dev (unwrap-map (indent dev-indent cljs-dev))
           :cljs-uberjar (unwrap-map (indent uberjar-indent cljs-uberjar))))]
    state))
