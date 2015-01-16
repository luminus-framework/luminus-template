(ns <<project-ns>>.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [clojure.java.io :as io]))

(def db-store (str (.getName (io/file ".")) "/site.db"))

(def db-spec
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     db-store
   :make-pool?  true
   :naming      {:keys   clojure.string/lower-case
                 :fields clojure.string/upper-case}})

(defqueries "sql/functions.sql" {:connection db-spec})