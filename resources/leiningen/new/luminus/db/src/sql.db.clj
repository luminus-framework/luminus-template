(ns <<project-ns>>.db.core<% if embedded-db %>
  (:require
    [clojure.java.jdbc :as jdbc]
    [conman.core :as conman]
    [java-time.pre-java8 :as jt]
    [mount.core :refer [defstate]]
    [<<project-ns>>.config :refer [env]])<% endif %><% ifequal db-type "postgres" %>
  (:require
    [cheshire.core :refer [generate-string parse-string]]
    [clojure.java.jdbc :as jdbc]
    [clojure.tools.logging :as log]
    [conman.core :as conman]
    [java-time :as jt]
    [java-time.pre-java8]
    [<<project-ns>>.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import org.postgresql.util.PGobject
           java.sql.Array
           clojure.lang.IPersistentMap
           clojure.lang.IPersistentVector
           [java.sql
            BatchUpdateException
            PreparedStatement])<% endifequal %><% ifequal db-type "mysql" %>
  (:require
    [clj-time.jdbc]
    [clojure.java.jdbc :as jdbc]
    [clojure.tools.logging :as log]
    [conman.core :as conman]
    [java-time.pre-java8]
    [<<project-ns>>.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import [java.sql
            BatchUpdateException
            PreparedStatement])<% endifequal %>)
<% if embedded-db %>
(defstate ^:dynamic *db*
          :start (conman/connect! {:jdbc-url (env :database-url)})
          :stop (conman/disconnect! *db*))
<% else %>(defstate ^:dynamic *db*
  :start (if-let [jdbc-url (env :database-url)]
           (conman/connect! {:jdbc-url jdbc-url})
           (do
             (log/warn "database connection URL was not found, please set :database-url in your config, e.g: dev-config.edn")
             *db*))
  :stop (conman/disconnect! *db*))
<% endif %>
(conman/bind-connection *db* "sql/queries.sql")

<% ifequal db-type "postgres" %>
<% include db/src/postgres-fragment.clj %>
<% else %>
(extend-protocol jdbc/IResultSetReadColumn
<% include db/src/datetime-deserializers.clj %>)

(extend-protocol jdbc/ISQLValue
<% include db/src/datetime-serializers.clj %>)
<% endifequal %>
