(ns <<project-ns>>.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [environ.core :refer [env]]
    [to-jdbc-uri.core :refer [to-jdbc-uri]]))

(def db-spec
  {:classname   "org.h2.Driver"
   :connection-uri (to-jdbc-uri (:database-url env))
   :make-pool?  true
   :naming      {:keys   clojure.string/lower-case
                 :fields clojure.string/upper-case}})

(defqueries "sql/queries.sql" {:connection db-spec})