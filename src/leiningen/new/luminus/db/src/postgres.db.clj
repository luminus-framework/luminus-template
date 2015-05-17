(ns <<project-ns>>.db.core
  (:require
    [clojure.java.jdbc :as jdbc]
    [yesql.core :refer [defqueries]]
    [cheshire.core :refer [parse-string]])
  (:import org.postgresql.util.PGobject
           org.postgresql.jdbc4.Jdbc4Array
           [java.sql Date Timestamp PreparedStatement]))

(def db-spec
  {:subprotocol "postgresql"
   :subname "//localhost/<<sanitized>>"
   :user "db_user_name_here"
   :password "db_user_password_here"})

(defqueries "sql/queries.sql" {:connection db-spec})

(defn to-date [sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol jdbc/IResultSetReadColumn
  Date
  (result-set-read-column [v _ _] (to-date v))

  Timestamp
  (result-set-read-column [v _ _] (to-date v))

  Jdbc4Array
  (result-set-read-column [v _ _] (vec (.getArray v)))

  PGobject
  (result-set-read-column [v _ _] (str v)))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (Timestamp. (.getTime v)))))

(extend-protocol jdbc/IResultSetReadColumn
  PGobject
  (result-set-read-column [pgobj _metadata _index]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "json" (parse-string value true)
        "jsonb" (parse-string value true)
        :else value))))