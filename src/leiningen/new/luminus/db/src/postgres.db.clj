(ns <<project-ns>>.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [clojure.java.jdbc :as jdbc])
  (:import [java.sql PreparedStatement]))

(def db-spec
  {:subprotocol "postgresql"
   :subname "//localhost/<<sanitized>>"
   :user "db_user_name_here"
   :password "db_user_password_here"})

(defqueries "sql/queries.sql" {:connection db-spec})

(defn to-date [sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Date
  (result-set-read-column [v _ _] (to-date v))

  java.sql.Timestamp
  (result-set-read-column [v _ _] (to-date v))

  org.postgresql.jdbc4.Jdbc4Array
  (result-set-read-column [v _ _] (vec (.getArray v)))

  org.postgresql.util.PGobject
  (result-set-read-column [v _ _] (str v)))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (java.sql.Timestamp. (.getTime v)))))