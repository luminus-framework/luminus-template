(ns leiningen.new.graphql
  (:require [leiningen.new.common :refer :all]))

(def graphql-assets
  [["{{backend-path}}/{{sanitized}}/routes/services/graphql.clj" "graphql/src/graphql.clj"]
   ["{{resource-path}}/html/graphiql.html" "graphql/resources/html/graphiql.html"]
   ["{{resource-path}}/graphql/schema.edn" "graphql/resources/schema.edn"]])

(def graphql-dependencies
  [['com.walmartlabs/lacinia "0.28.0"]
   ['org.clojure/data.json "0.2.6"]])

(defn graphql-features [[assets options :as state]]
  (if (some #{"+graphql"} (:features options))
    [(into assets graphql-assets)
     (-> options
         (append-options :dependencies graphql-dependencies)
         (assoc :graphql true))]
    state))
