(ns leiningen.new.lacinia
  (:require [leiningen.new.common :refer :all]))

(def conflicting-assets
  ["layout.clj"
   "home.clj"
   "screen.css"
   "luminus.png"
   "docs.md"
   ".html"])

(def conflicting-features
  #{"+swagger"})

(def lacinia-assets
  [["src/clj/{{sanitized}}/routes/services.clj" "lacinia/src/services.clj"]
   ["resources/schema.edn" "lacinia/resources/schema.edn"]])

(def lacinia-dependencies
  [['metosin/compojure-api "1.1.11"]
   ['com.walmartlabs/lacinia "0.21.0"]])

(def required-features
  [])

(defn update-features [features]
  (-> features
      (clojure.set/difference conflicting-features)
      (into required-features)))

(defn update-assets [assets]
  (reduce #(remove-conflicting-assets %1 %2) assets conflicting-assets))

(defn lacinia-features [[assets options :as state]]
  (if (some #{"+lacinia"} (:features options))
    (do
      (when-let [conflicts (not-empty (clojure.set/intersection conflicting-features (:features options)))]
        (println "ignoring conflicting features" (clojure.string/join ", " conflicts)))
      [(into assets lacinia-assets)
       (-> options
           (append-options :dependencies lacinia-dependencies)
           (update :features update-features)
           (assoc :lacinia true
                  :service-required
                    (indent require-indent
                        [[(symbol (str (:project-ns options) ".routes.services")) :refer ['service-routes]]])
                   :service-routes
                    (indent dev-indent ["#'service-routes"])))])
    state))
