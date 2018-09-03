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

(defn service-features [[assets options :as state]]
  (if (some #{"+service"} (:features options))
    (do
      (when-let [conflicts (not-empty (clojure.set/intersection conflicting-features (:features options)))]
        (println "ignoring conflicting features" (clojure.string/join ", " conflicts)))
      [(apply remove-conflicting-assets assets conflicting-assets)
       (-> options
           (update :features update-features)
           (assoc :service true))])
    state))
