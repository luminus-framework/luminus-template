(ns leiningen.new.graphql
  (:require [leiningen.new.common :refer :all]))

(def conflicting-assets
  [])

(def conflicting-features
  #{"+swagger"})

(def graphql-assets
  [["{{backend-path}}/{{sanitized}}/routes/services.clj" "graphql/src/services.clj"]
   ["{{resource-path}}/templates/graphiql.html" "graphql/resources/graphiql.html"]
   ["{{resource-path}}/graphql/schema.edn" "graphql/resources/schema.edn"]])

(def graphql-dependencies
  [['metosin/compojure-api "1.1.12"]
   ['com.walmartlabs/lacinia "0.25.0"]])

(def required-features
  [])

(defn update-features [features]
  (-> features
      (clojure.set/difference conflicting-features)
      (into required-features)))

(defn update-assets [assets]
  (reduce #(remove-conflicting-assets %1 %2) assets conflicting-assets))

(defn graphql-features [[assets options :as state]]
  (if (some #{"+graphql"} (:features options))
    (do
      (when-let [conflicts (not-empty (clojure.set/intersection conflicting-features (:features options)))]
        (println "ignoring conflicting features" (clojure.string/join ", " conflicts)))
      [(into assets graphql-assets)
       (-> options
           (append-options :dependencies graphql-dependencies)
           (update :features update-features)
           (assoc :graphql true
                  :service-required
                    (indent require-indent
                        [[(symbol (str (:project-ns options) ".routes.services")) :refer ['service-routes]]])
                   :service-routes
                    (indent dev-indent ["#'service-routes"])))])
    state))
