(ns <<project-ns>>.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [clojure.java.jdbc :as jdbc]
    [environ.core :refer [env]])
  (:import [java.sql PreparedStatement]))

(defonce db-spec (atom nil))

(defqueries "sql/queries.sql")

(defn connect! []
  (reset! db-spec {:connection-uri (env :database-url)}))

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