(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
   [<<project-ns>>.config :refer [env]]
    [clojure.pprint]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [mount.core :as mount]<% if figwheel %>
    [<<project-ns>>.figwheel :refer [start-fw stop-fw cljs]]<% endif %>
    [<<project-ns>>.core :refer [start-app]]<% if relational-db %>
    [<<project-ns>>.db.core]
    [conman.core :as conman]
    [luminus-migrations.core :as migrations]<% endif %>))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(add-tap (bound-fn* clojure.pprint/pprint))

(defn start
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'<<project-ns>>.core/repl-server))

(defn stop
  "Stops application."
  []
  (mount/stop-except #'<<project-ns>>.core/repl-server))

(defn restart
  "Restarts application."
  []
  (stop)
  (start))
<% if relational-db %>
(defn restart-db
  "Restarts database."
  []
  (mount/stop #'<<project-ns>>.db.core/*db*)
  (mount/start #'<<project-ns>>.db.core/*db*)
  (binding [*ns* (the-ns '<<project-ns>>.db.core)]
    (conman/bind-connection <<project-ns>>.db.core/*db* "sql/queries.sql")))

(defn reset-db
  "Resets database."
  []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate
  "Migrates database up for all outstanding migrations."
  []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback
  "Rollback latest database migration."
  []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration
  "Create a new up and down migration file with a generated timestamp and `name`."
  [name]
  (migrations/create name (select-keys env [:database-url])))
<% endif %>

