(ns leiningen.new.cljs
  (:require [leiningen.new.common :refer :all]))

(def cljs-assets
  [["src-cljs/<<sanitized>>/core.cljs" "cljs/src/cljs/core.cljs"]
   ["env/dev/cljs/<<sanitized>>/dev.cljs" "cljs/env/dev/cljs/app.cljs"]
   ["env/prod/cljs/<<sanitized>>/prod.cljs" "cljs/env/prod/cljs/app.cljs"]
   ["resources/templates/home.html" "cljs/templates/home.html"]])

(defn remove-conflicting-assets [assets]
  (remove #(and (coll? %)
                (.endsWith (second %) ".html")) assets))

(def cljs-dependencies
  [['org.clojure/clojurescript "0.0-2719" :scope "provided"]
   ['reagent-forms "0.4.3"]
   ['reagent-utils "0.1.2"]
   ['secretary "1.2.1"]
   ['cljs-ajax "0.3.9"]])

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

(defn cljs-features [[assets options :as state]]
  (if (some #{"+cljs"} (:features options))
    [(into (remove-conflicting-assets assets) cljs-assets)
     (-> options
         (append-options :dependencies cljs-dependencies)
         (append-options :plugins [['lein-cljsbuild "1.0.4"]])
         (assoc
           :cljs-build (indent root-indent cljs-build)
           :cljs-dev (unwrap-map (indent dev-indent cljs-dev))
           :cljs-uberjar (unwrap-map (indent uberjar-indent cljs-uberjar))))]
    state))
