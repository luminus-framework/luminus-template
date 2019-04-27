(ns leiningen.new.shadow-cljs
  (:require [leiningen.new.common :refer :all]))

(def shadow-cljs-assets [["package.json" "shadow_cljs/package.json"]
                         ["shadow-cljs.edn" "shadow_cljs/shadow-cljs.edn"]])

(def shadow-cljs-dependencies '[[com.google.javascript/closure-compiler-unshaded "v20190325"]
                                [org.clojure/google-closure-library "0.0-20190213-2033d5d9"]
                                [thheller/shadow-cljs "2.8.31"]])

(defn shadow-cljs-features [[assets options :as state]]
  (if (some #{"+shadow-cljs"} (:features options))
    [(into assets shadow-cljs-assets)
     (-> options
         (assoc :shadow-cljs true)
         (append-options :dependencies shadow-cljs-dependencies))]
    state))

;; TODO: review boot and see if can integrate it w/ boot