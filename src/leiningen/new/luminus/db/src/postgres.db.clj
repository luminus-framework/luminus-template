(ns <<project-ns>>.db.core
  (:require
    [clojure.java.jdbc :as jdbc]
    [yesql.core :refer [defqueries]]
    [cheshire.core :refer [parse-string generate-string]])
  (:import java.sql.PreparedStatement
           org.postgresql.util.PGobject))

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

(defn to-pg-json [value]
  (doto (PGobject.)
    (.setType "jsonb")
    (.setValue (generate-string value))))

(extend-protocol jdbc/ISQLValue
  clojure.lang.IPersistentMap
  (sql-value [value] (to-pg-json value))
  clojure.lang.IPersistentVector
  (sql-value [value] (to-pg-json value)))

(extend-protocol jdbc/IResultSetReadColumn
  PGobject
  (result-set-read-column [pgobj _metadata _index]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "json" (parse-string value true)
        "jsonb" (parse-string value true)
        :else value))))