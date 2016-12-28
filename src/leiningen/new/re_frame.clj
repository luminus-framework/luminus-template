(ns leiningen.new.re-frame
  (:require [leiningen.new.common :refer :all]))

(def re-frame-assets
  [["src/cljs/{{sanitized}}/core.cljs" "reframe/src/cljs/core.cljs"]
   ["src/cljs/{{sanitized}}/db.cljs" "reframe/src/cljs/db.cljs"]
   ["src/cljs/{{sanitized}}/handlers.cljs" "reframe/src/cljs/handlers.cljs"]
   ["src/cljs/{{sanitized}}/subscriptions.cljs" "reframe/src/cljs/subscriptions.cljs"]])

(defn re-frame-features [[assets options :as state]]
  (if (some #{"+re-frame"} (:features options))
    [(into (remove-conflicting-assets assets "core.cljs") re-frame-assets)
     (-> options
         (assoc :re-frame true)
         (append-options :dependencies [['re-frame "0.9.1"]]))]
    state))
