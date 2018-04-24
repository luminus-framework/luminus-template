(ns user
  (:require [<<project-ns>>.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]<% if figwheel %>
            [<<project-ns>>.figwheel :refer [start-fw stop-fw cljs]]<% endif %>
            [<<project-ns>>.core :refer [start-app]]<% if relational-db %>
            [conman.core :as conman]
            [luminus-migrations.core :as migrations]<% endif %>))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'<<project-ns>>.core/repl-server))

(defn stop []
  (mount/stop-except #'<<project-ns>>.core/repl-server))

(defn restart []
  (stop)
  (start))
<% if relational-db %>
(defn restart-db []
  (mount/stop #'<<project-ns>>.db.core/*db*)
  (mount/start #'<<project-ns>>.db.core/*db*)
  (binding [*ns* '<<project-ns>>.db.core]
    (conman/bind-connection <<project-ns>>.db.core/*db* "sql/queries.sql")))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))
<% endif %>

