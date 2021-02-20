(ns leiningen.new.re-frame
  (:require [leiningen.new.common :refer :all]))

(def re-frame-assets
  [["{{client-path}}/{{sanitized}}/core.cljs" "reframe/src/cljs/core.cljs"]
   ["{{client-path}}/{{sanitized}}/events.cljs" "reframe/src/cljs/events.cljs"]])

(defn re-frame-features [[assets options :as state]]
  (if (some #{"+re-frame"} (:features options))
    [(into (remove-conflicting-assets assets "core.cljs")
           re-frame-assets)
     (-> options
         (assoc :re-frame true)
         (append-options :dependencies [['re-frame "1.1.2"]
                                        ['day8.re-frame/http-fx "0.2.2"]])
         (append-options :dev-dependencies [['re-frisk "1.3.10"]
                                            #_['day8.re-frame/re-frame-10x "0.7.0"]]))]
    state))
