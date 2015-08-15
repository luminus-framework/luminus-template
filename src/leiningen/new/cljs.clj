(ns leiningen.new.cljs
  (:require [leiningen.new.common :refer :all]))

(def cljs-assets
  [["src-cljs/{{sanitized}}/core.cljs" "cljs/src/cljs/core.cljs"]
   ["env/dev/cljs/{{sanitized}}/dev.cljs" "cljs/env/dev/cljs/app.cljs"]
   ["env/prod/cljs/{{sanitized}}/prod.cljs" "cljs/env/prod/cljs/app.cljs"]
   ["resources/templates/home.html" "cljs/templates/home.html"]
   ["resources/templates/error.html" "core/resources/templates/error.html"]])

(def cljs-dependencies
  [['org.clojure/clojurescript "1.7.107" :scope "provided"]
   ['org.clojure/tools.reader "0.9.2"]
   ['reagent "0.5.0"]
   ['cljsjs/react "0.13.3-1"]
   ['reagent-forms "0.5.5"]
   ['reagent-utils "0.1.5"]
   ['secretary "1.2.3"]
   ['org.clojure/core.async "0.1.346.0-17112a-alpha"]
   ['cljs-ajax "0.3.14"]])

(def clean-targets [:target-path
                    [:cljsbuild :builds :app :compiler :output-dir]
                    [:cljsbuild :builds :app :compiler :output-to]])

(def cljs-dev-dependencies
  [['lein-figwheel "0.3.7"]
   ['org.clojure/tools.nrepl "0.2.10"]])

(def cljs-build
  {:builds {:app {:source-paths ["src-cljs"]
                  :compiler     {:output-to     "resources/public/js/app.js"
                                 :output-dir    "resources/public/js/out"
                                 :externs       ["react/externs/react.js"]
                                 :optimizations :none
                                 :pretty-print  true}}}})

(def cljs-uberjar
  {:hooks     ['leiningen.cljsbuild]
   :cljsbuild {:jar    true
               :builds {:app
                        {:source-paths ["env/prod/cljs"]
                         :compiler     {:optimizations :advanced
                                        :pretty-print  false}}}}})

(def cljs-dev
  {:cljsbuild {:builds
               {:app
                {:compiler {:source-map true}
                 :source-paths ["env/dev/cljs"]}}}})

(defn figwheel [{:keys [project-ns]}]
  {:http-server-root "public"
   :server-port 3449
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :ring-handler (symbol (str project-ns ".handler/app"))})

(defn cljs-features [[assets options :as state]]
  (if (some #{"+cljs"} (:features options))
    [(into (remove-conflicting-assets assets ".html") cljs-assets)
     (-> options
         (append-options :dependencies cljs-dependencies)
         (append-options :dev-dependencies cljs-dev-dependencies)
         (append-options :plugins [['lein-cljsbuild "1.0.6"]])
         (append-options :dev-plugins [['lein-figwheel "0.3.7"]])
         (update-in [:clean-targets] (fnil into []) clean-targets)
         (update-in [:gitignore] conj "resources/public/js")
         (assoc
           :cljs true
           :cljs-build (indent root-indent cljs-build)
           :figwheel (indent dev-indent (figwheel options))
           :cljs-dev (unwrap-map (indent dev-indent cljs-dev))
           :cljs-uberjar (unwrap-map (indent uberjar-indent cljs-uberjar))))]
    state))
