(ns <<project-ns>>.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [environ.core :refer [env]]))

(defonce db-spec (atom nil))

(defqueries "sql/queries.sql")

(defn connect! []
  (reset! db-spec
          {:classname   "org.h2.Driver"
           :connection-uri (:database-url env)
           :make-pool?     true
           :naming         {:keys   clojure.string/lower-case
                            :fields clojure.string/upper-case}}))
