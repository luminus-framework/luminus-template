(ns leiningen.new.hoplon
  (:require [leiningen.new.common :refer :all]))

(def reagent-assets
  [["src/cljs/{{sanitized}}/core.cljs" "hoplon/src/cljs/core.cljs"]
   ["src/cljs/{{sanitized}}/ajax.cljs" "hoplon/src/cljs/ajax.cljs"]])

(def reagent-dependencies
  [['hoplon "6.0.0-alpha17"]
   ['cljsjs/jquery "2.2.4-0"]
   ['secretary "1.2.3"]
   ['cljs-ajax "0.5.8"]])

(defn hoplon-features [[assets options :as state]]
  (if (some #{"+hoplon"} (:features options))
    [(into (remove-conflicting-assets assets "core.cljs") reagent-assets)
     (-> options
         (assoc :hoplon true)
         (append-options :dependencies reagent-dependencies))]
    state))
