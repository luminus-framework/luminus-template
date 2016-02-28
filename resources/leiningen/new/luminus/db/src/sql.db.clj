(ns <<project-ns>>.db.core<% if embedded-db %>
  (:require
    [conman.core :as conman]
    [mount.core :refer [defstate]]
    [<<project-ns>>.config :refer [env]])<% endif %><% ifequal db-type "postgres" %>
  (:require
    [cheshire.core :refer [generate-string parse-string]]
    [clojure.java.jdbc :as jdbc]
    [conman.core :as conman]
    [<<project-ns>>.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import org.postgresql.util.PGobject
           org.postgresql.jdbc4.Jdbc4Array
           clojure.lang.IPersistentMap
           clojure.lang.IPersistentVector
           [java.sql
            BatchUpdateException
            Date
            Timestamp
            PreparedStatement])<% endifequal %><% ifequal db-type "mysql" %>
  (:require
    [clojure.java.jdbc :as jdbc]
    [conman.core :as conman]
    [<<project-ns>>.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import [java.sql
            BatchUpdateException
            PreparedStatement])<% endifequal %>)
<% ifequal db-type "sqlite"%>
(defstate ^:dynamic *db*
          :start (conman/connect!
                   {:datasource
                    (doto (org.sqlite.SQLiteDataSource.)
                          (.setUrl (-> env :database :url)))})
          :stop (conman/disconnect! *db*))
<% endifequal %><% ifequal db-type "h2"%>
(defstate ^:dynamic *db*
          :start (conman/connect!
                   {:datasource
                    (doto (org.h2.jdbcx.JdbcDataSource.)
                          (.setURL (-> env :database :url))
                          (.setUser "")
                          (.setPassword ""))})
          :stop (conman/disconnect! *db*))
<% endifequal %><% ifequal db-type "postgres" %>
(defstate ^:dynamic *db*
          :start (conman/connect!
                   {:adapter    :postgresql
                    :init-size  1
                    :min-idle   1
                    :max-idle   4
                    :max-active 32
                    :jdbc-url   (-> env :database :url)})
          :stop (conman/disconnect! *db*))
<% endifequal %><% ifequal db-type "mysql" %>
(defstate ^:dynamic *db*
          :start (conman/connect!
                   {:adapter    :mysql
                    :init-size  1
                    :min-idle   1
                    :max-idle   4
                    :max-active 32
                    :jdbc-url   (-> env :database :url)})
          :stop (conman/disconnect! *db*))
<% endifequal %>
(conman/bind-connection *db* "sql/queries.sql")
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
  (set-parameter [v ^PreparedStatement stmt ^long idx]
    (.setTimestamp stmt idx (Timestamp. (.getTime v)))))

(extend-type clojure.lang.IPersistentVector
  jdbc/ISQLParameter
  (set-parameter [v ^java.sql.PreparedStatement stmt ^long idx]
    (let [conn      (.getConnection stmt)
          meta      (.getParameterMetaData stmt)
          type-name (.getParameterTypeName meta idx)]
      (if-let [elem-type (when (= (first type-name) \_) (apply str (rest type-name)))]
        (.setObject stmt idx (.createArrayOf conn elem-type (to-array v)))
        (.setObject stmt idx v)))))

(defn to-pg-json [value]
  (doto (PGobject.)
    (.setType "jsonb")
    (.setValue (generate-string value))))

(extend-protocol jdbc/ISQLValue
  IPersistentMap
  (sql-value [value] (to-pg-json value))
  IPersistentVector
  (sql-value [value] (to-pg-json value)))<% endifequal %>
