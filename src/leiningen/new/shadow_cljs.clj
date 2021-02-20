(ns leiningen.new.shadow-cljs
  (:require [leiningen.new.common :refer :all]))

(def shadow-version "2.11.18")

(def shadow-cljs-dependencies [['com.google.javascript/closure-compiler-unshaded "v20200504" :scope "provided"]
                               ['org.clojure/google-closure-library "0.0-20191016-6ae1f72f" :scope "provided"]
                               #_['org.clojure/google-closure-library-third-party "0.0-20191016-6ae1f72f" :scope "provided"]
                               ['thheller/shadow-cljs shadow-version :scope "provided"]
                               ['org.clojure/core.async "1.3.610"]])

(def shadow-cljs-plugins [['lein-shadow "0.2.0"]])

(defn project-ns-symbol [project-ns suffix]
  (read-string (str project-ns suffix)))

;; Goal was to reproduce the same profiles as leiningen/cljsbuild approach
(defn shadow-cljs-config [{:keys [features project-ns]}]
  {:nrepl
   {:port 7002}

   :builds
   {:app  (merge
           {:target     :browser
            :output-dir "target/cljsbuild/public/js"
            :asset-path "/js"
            :modules    {:app
                         {:entries [(project-ns-symbol project-ns ".app")]}}
            :devtools   (merge {:watch-dir "resources/public"}
                               (when (some #{"+re-frame"} features)
                                 {:preloads ['re-frisk.preload #_'day8.re-frame-10x.preload]}))}
           (when (some #{"+re-frame"} features)
             {:dev {:closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}}}))

    :test {:target    :node-test
           :output-to "target/test/test.js"
           :autorun   true}}})

;; TODO: Hoplon?
(defn npm-deps [{:keys [features]}]
  []
  #_(cond-> [['shadow-cljs shadow-version]]
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
                :npm-dev-deps [['xmlhttprequest "1.8.0"]]
                :npm-deps (indent require-indent (npm-deps options)))
         (append-options :plugins shadow-cljs-plugins)
         (append-options :dependencies shadow-cljs-dependencies))]
    state))

;; TODO: review boot and see if can integrate it w/ boot
