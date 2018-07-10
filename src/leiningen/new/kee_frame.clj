(ns leiningen.new.kee-frame
  (:require [leiningen.new.common :refer :all]))

(def kee-frame-assets
  [["{{client-path}}/{{sanitized}}/core.cljs" "keeframe/src/cljs/core.cljs"]
   ["{{client-path}}/{{sanitized}}/effects.cljs" "keeframe/src/cljs/effects.cljs"]
   ["{{client-path}}/{{sanitized}}/routing.cljs" "keeframe/src/cljs/routing.cljs"]])

(def conflicting-assets
  ["core.cljs"
   "events.cljs"])

(defn update-assets [assets]
  (reduce #(remove-conflicting-assets %1 %2) assets conflicting-assets))

(defn kee-frame-features [[assets options :as state]]
  (if (some #{"+kee-frame"} (:features options))
    [(into (update-assets assets) kee-frame-assets)
     (-> options
         (assoc :kee-frame true)
         (append-options :dependencies [['kee-frame "0.2.4"]]))]
    state))
