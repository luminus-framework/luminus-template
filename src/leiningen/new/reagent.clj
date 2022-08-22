(ns leiningen.new.reagent
  (:require [leiningen.new.common :refer :all]))

(def reagent-assets
  [["{{client-path}}/{{sanitized}}/core.cljs" "reagent/src/cljs/core.cljs"]
   ["{{client-path}}/{{sanitized}}/ajax.cljs" "reagent/src/cljs/ajax.cljs"]])

(defn reagent-dependencies [{:keys [features]}]
  (into
    [['reagent "1.1.1"]
     ['cljs-ajax "0.8.4"]]
    (when (some #{"+figwheel"} features)
      [['cljsjs/react "17.0.2-0"]
       ['cljsjs/react-dom "17.0.2-0"]])))

(defn reagent-features [[assets options :as state]]
  (if (some #{"+reagent"} (:features options))
    [(into (remove-conflicting-assets assets "core.cljs") reagent-assets)
     (-> options
         (assoc :reagent true)
         (append-options :dependencies (reagent-dependencies options)))]
    state))
