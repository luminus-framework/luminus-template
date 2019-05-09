(ns leiningen.new.shadow-cljs
  (:require [leiningen.new.common :refer :all]))

(def shadow-cljs-dependencies '[[com.google.javascript/closure-compiler-unshaded "v20190325"]
                                [org.clojure/google-closure-library "0.0-20190213-2033d5d9"]
                                [thheller/shadow-cljs "2.8.31"]])

(def shadow-cljs-plugins [['lein-shadow "0.1.1"]])

(defn project-ns-symbol [project-ns suffix]
  (read-string (str project-ns suffix)))

;; Goal was to reproduce the same profiles as leiningen/cljsbuild approach
(defn shadow-cljs-config [{:keys [project-ns]}]
  {:lein
   true

   :nrepl
   {:port 7002}

   :builds
   {:app  {:target        :browser
           :output-dir    "target/cljsbuild/public/js"
           :asset-path    "/js"
           :modules       {:app
                           {:entries [(project-ns-symbol project-ns ".app")]}}
           :optimizations :none
           :devtools      {:watch-dir  "resources/public"
                           :preloads   ['devtools.preload]
                           :after-load (project-ns-symbol project-ns ".core/mount-components")}}

    :test {:target    :browser-test
           :test-dir  "target/test.js"
           :runner-ns (project-ns-symbol project-ns ".doo-runner")}

    :min  {:target           :browser
           :output-dir       "target/cljsbuild/public/js"
           :asset-path       "/js"
           :optimizations    :advanced
           :modules          {:app
                              {:entries [(project-ns-symbol project-ns ".app")]}}
           :compiler-options {:closure-warnings {:global-this :off}
                              :infer-externs    :auto}}}})

;; TODO: Hoplon?
(defn npm-deps [{:keys [features]}]
  (cond-> [['shadow-cljs "2.8.31"]]

          (some #{"+reagent"} features)
          ((fnil into [])
            [['create-react-class "15.6.3"]
             ['react "16.8.6"]
             ['react-dom "16.8.6"]])))

(defn shadow-cljs-features [[assets options :as state]]
  (if (some #{"+shadow-cljs"} (:features options))
    [assets
     (-> options
         (assoc :shadow-cljs true
                :shadow-cljs-config (indent root-indent (shadow-cljs-config options))
                :npm-deps (indent require-indent (npm-deps options)))
         (append-options :plugins shadow-cljs-plugins)
         (append-options :dependencies shadow-cljs-dependencies))]
    state))

;; TODO: review boot and see if can integrate it w/ boot