(ns <<project-ns>>.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [clojure.java.jdbc :as jdbc]
    [environ.core :refer [env]]
    [to-jdbc-uri.core :refer [to-jdbc-uri]])
  (:import [java.sql PreparedStatement]))

(def db-spec
  {:connection-uri (to-jdbc-uri (env :database-url))})

(defqueries "sql/queries.sql" {:connection db-spec})

(defn to-date [sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Date
  (result-set-read-column [v _ _] (to-date v))

  java.sql.Timestamp
  (result-set-read-column [v _ _] (to-date v)))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (java.sql.Timestamp. (.getTime v)))))