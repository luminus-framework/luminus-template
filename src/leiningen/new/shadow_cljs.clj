(ns leiningen.new.shadow-cljs
  (:require [leiningen.new.common :refer :all]))

(def shadow-cljs-assets [["package.json" "shadow_cljs/package.json"]])

(def shadow-cljs-dependencies '[[com.google.javascript/closure-compiler-unshaded "v20190325"]
                                [org.clojure/google-closure-library "0.0-20190213-2033d5d9"]
                                [thheller/shadow-cljs "2.8.31"]])

(def shadow-cljs-plugins [['lein-shadow "0.1.0"]])

(defn project-ns-symbol [project-ns suffix]
  (read-string (str project-ns suffix)))

(defn shadow-cljs-config [{:keys [project-ns]}]
  {:lein
   true

   :nrepl
   {:port 7002}

   :builds
   {:app  {:target        :browser
           :output-dir    "target/cljsbuild/public/js/out"
           :asset-path    "/js/out"
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

(defn shadow-cljs-features [[assets options :as state]]
  (if (some #{"+shadow-cljs"} (:features options))
    [(into assets shadow-cljs-assets)
     (-> options
         (assoc :shadow-cljs true
                :shadow-cljs-config (indent root-indent (shadow-cljs-config options)))
         (append-options :plugins shadow-cljs-plugins)
         (append-options :dependencies shadow-cljs-dependencies))]
    state))

;; TODO: review boot and see if can integrate it w/ boot