(ns <<project-ns>>.db.core<% if embedded-db %>
  (:require
    [next.jdbc.date-time]
    [next.jdbc.result-set]
    [conman.core :as conman]
    [java-time.pre-java8 :as jt]
    [mount.core :refer [defstate]]
    [<<project-ns>>.config :refer [env]])
  (:import (java.sql Date Time Timestamp))<% endif %><% ifequal db-type "postgres" %>
  (:require
    [cheshire.core :refer [generate-string parse-string]]
    [next.jdbc.date-time]
    [next.jdbc.prepare]
    [next.jdbc.result-set]
    [clojure.tools.logging :as log]
    [conman.core :as conman]
    [java-time :as jt]
    [java-time.pre-java8]
    [<<project-ns>>.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import (org.postgresql.util PGobject)
           (clojure.lang IPersistentMap IPersistentVector)
           (java.sql Array Date PreparedStatement Time Timestamp))<% endifequal %><% ifequal db-type "mysql" %>
  (:require
    [next.jdbc.date-time]
    [next.jdbc.result-set]
    [clojure.tools.logging :as log]
    [conman.core :as conman]
    [java-time.pre-java8 :as jt]
    [<<project-ns>>.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import (java.sql Timestamp Date Time))<% endifequal %>)
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
(extend-protocol next.jdbc.result-set/ReadableColumn
<% include db/src/datetime-deserializers.clj %>)
