(ns leiningen.new.service
  (:require [leiningen.new.common :refer :all]))

(def conflicting-assets
  ["layout.clj"
   "home.clj"
   "screen.css"
   "luminus.png"
   "docs.md"
   ".html"])

(def conflicting-features
  #{"+cljs" "+site"})

(def required-features
  ["+swagger"])

(defn update-features [features]
  (-> features
      (clojure.set/difference conflicting-features)
      (into required-features)))

(defn update-assets [assets]
  (reduce #(remove-conflicting-assets %1 %2) assets conflicting-assets))

(defn service-features [[assets options :as state]]
  (if (some #{"+service"} (:features options))
    [(update-assets assets)
     (-> options
         (update :features update-features)
         (assoc :service true))]
    state))
