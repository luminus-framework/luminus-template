(ns leiningen.new.shadow-cljs
  (:require [leiningen.new.common :refer :all]))

(def shadow-version "2.14.3")

(def shadow-cljs-dependencies [['thheller/shadow-cljs shadow-version :scope "provided"]
                               ['org.clojure/core.async "1.3.618"]])

(def shadow-assets
  [["package.json" "shadow/package.json"]
   ["shadow-cljs.edn" "shadow/shadow-cljs.edn"]])

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
    [(into assets shadow-assets)
     (-> options
         (update :plugins (fnil conj []) ['lein-shell "0.5.0"])
         (assoc :shadow-cljs true
                :shadow-uberjar-prep ":prep-tasks [\"compile\" [\"shell\" \"shadow-cljs\" \"release\" \"app\"]]"
                :npm-dev-deps [['xmlhttprequest "1.8.0"]]
                :npm-deps (indent require-indent (npm-deps options)))
         (append-options :dependencies shadow-cljs-dependencies))]
    state))

;; TODO: review boot and see if can integrate it w/ boot
