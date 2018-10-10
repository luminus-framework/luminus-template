(ns leiningen.new.kee-frame
  (:require [leiningen.new.common :refer :all]))

(def kee-frame-assets
  [["{{client-path}}/{{sanitized}}/core.cljs" "keeframe/src/cljs/core.cljs"]
   ["{{client-path}}/{{sanitized}}/routing.cljs" "keeframe/src/cljs/routing.cljs"]])

(def conflicting-assets
  ["core.cljs"
   "events.cljs"])

(defn kee-frame-features [[assets options :as state]]
  (if (some #{"+kee-frame"} (:features options))
    [(-> (apply remove-conflicting-assets assets conflicting-assets)
         (into kee-frame-assets))
     (-> options
         (assoc :kee-frame true)
         (append-options :dependencies [['kee-frame "0.3.0"]]))]
    state))
