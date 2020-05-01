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
         (append-options :dependencies [['re-frame "0.12.0"]
                                        ['day8.re-frame/http-fx "0.1.6"]])
         (append-options :dev-dependencies [['re-frisk "1.2.0"]]))]
    state))
