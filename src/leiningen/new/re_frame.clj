(ns leiningen.new.re-frame
  (:require [leiningen.new.common :refer :all]))

(def re-frame-assets
  [["{{client-path}}/{{sanitized}}/core.cljs" "reframe/src/cljs/core.cljs"]
   ["{{client-path}}/{{sanitized}}/db.cljs" "reframe/src/cljs/db.cljs"]
   ["{{client-path}}/{{sanitized}}/events.cljs" "reframe/src/cljs/events.cljs"]])

(defn re-frame-features [[assets options :as state]]
  (if (some #{"+re-frame"} (:features options))
    [(into (remove-conflicting-assets assets "core.cljs") re-frame-assets)
     (-> options
         (assoc :re-frame true)
         (append-options :dependencies [['re-frame "0.10.5"]])
         (append-options :dev-dependencies [['day8.re-frame/re-frame-10x "0.3.3"]]))]
    state))
