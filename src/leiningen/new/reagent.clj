(ns leiningen.new.reagent
  (:require [leiningen.new.common :refer :all]))

(def reagent-assets
  [["{{client-path}}/{{sanitized}}/core.cljs" "reagent/src/cljs/core.cljs"]
   ["{{client-path}}/{{sanitized}}/ajax.cljs" "reagent/src/cljs/ajax.cljs"]])

(def reagent-dependencies
  [['reagent "0.8.1"]
   ['secretary "1.2.3"]
   ['cljs-ajax "0.7.4"]
   ['baking-soda "0.2.0"
    :exclusions
    ['cljsjs/react-bootstrap]]
   ['cljsjs/react-transition-group "2.4.0-0"]
   ['cljsjs/react-popper "0.10.4-0"]])

(defn reagent-features [[assets options :as state]]
  (if (some #{"+reagent"} (:features options))
    [(into (remove-conflicting-assets assets "core.cljs") reagent-assets)
     (-> options
         (assoc :reagent true)
         (append-options :dependencies reagent-dependencies))]
    state))
