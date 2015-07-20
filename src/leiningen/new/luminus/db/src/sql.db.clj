(ns <<project-ns>>.db.core<% ifequal db-type "h2" %>
  (:require
    [clojure.java.jdbc :as jdbc]
    [yesql.core :refer [defqueries]]
    [taoensso.timbre :as timbre]
    [environ.core :refer [env]])
  (:import java.sql.BatchUpdateException)<% endifequal %><% ifequal db-type "postgres" %>
  (:require
    [clojure.java.jdbc :as jdbc]
    [to-jdbc-uri.core :refer [to-jdbc-uri]]
    [clj-dbcp.core :as dbcp]
    [yesql.core :refer [defqueries]]
    [cheshire.core :refer [generate-string parse-string]]
    [taoensso.timbre :as timbre]
    [environ.core :refer [env]])
  (:import org.postgresql.util.PGobject
           org.postgresql.jdbc4.Jdbc4Array
           clojure.lang.IPersistentMap
           clojure.lang.IPersistentVector
           [java.sql BatchUpdateException
            Date
            Timestamp
            PreparedStatement])<% endifequal %><% ifequal db-type "mysql" %>
  (:require
    [yesql.core :refer [defqueries]]
    [clj-dbcp.core :as dbcp]
    [clojure.java.jdbc :as jdbc]
    [to-jdbc-uri.core :refer [to-jdbc-uri]]
    [taoensso.timbre :as timbre]
    [environ.core :refer [env]])
  (:import [java.sql BatchUpdateException
            PreparedStatement])<% endifequal %>)

(defonce conn (atom nil))

(defqueries "sql/queries.sql")<% ifequal db-type "postgres" %>

(def pool-spec
  {:adapter    :postgresql
   :init-size  1
   :min-idle   1
   :max-idle   4
   :max-active 32})<% endifequal %><% ifequal db-type "mysql" %>

(def pool-spec
  {:adapter    :mysql
   :init-size  1
   :min-idle   1
   :max-idle   4
   :max-active 32})<% endifequal %>
 
(defn connect! []
  (try
    (reset!
      conn<% ifequal db-type "h2" %>
      {:classname   "org.h2.Driver"
       :connection-uri (:database-url env)
       :make-pool?     true
       :naming         {:keys   clojure.string/lower-case
                        :fields clojure.string/upper-case}}<% else %>
      {:datasource
       (dbcp/make-datasource
         (assoc pool-spec
           :jdbc-url (to-jdbc-uri (env :database-url))))}<% endifequal %>)
    (catch Exception e
      (timbre/error "Error occured while connecting to the database!" e))))

(defn disconnect! []<% ifunequal db-type "h2" %>
  (when-let [conn (:connection conn)]
    (when-not (.isClosed conn)
      (.close conn)))<% endifunequal %>)

(defn run
  "executes a Yesql query using the given database connection and parameter map
  the parameter map defaults to an empty map and the database conection defaults
  to the conn atom"
  ([query-fn] (run query-fn {}))
  ([query-fn query-map] (run query-fn query-map @conn))
  ([query-fn query-map db]
   (try
     (query-fn query-map {:connection db})
     (catch BatchUpdateException e
       (throw (or (.getNextException e) e))))))
<% ifequal db-type "mysql" %>
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
<% endifequal %><% ifequal db-type "postgres" %>
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
  (result-set-read-column [pgobj _metadata _index]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "json" (parse-string value true)
        "jsonb" (parse-string value true)
        "citext" (str value)
        value))))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (Timestamp. (.getTime v)))))

(defn to-pg-json [value]
  (doto (PGobject.)
    (.setType "jsonb")
    (.setValue (generate-string value))))

(extend-protocol jdbc/ISQLValue
  IPersistentMap
  (sql-value [value] (to-pg-json value))
  IPersistentVector
  (sql-value [value] (to-pg-json value)))<% endifequal %>
