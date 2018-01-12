(ns user
  (:require <% if relational-db %>[luminus-migrations.core :as migrations]
            [<<project-ns>>.config :refer [env]]<% endif %>
            [mount.core :as mount]<% if figwheel %>
            [<<project-ns>>.figwheel :refer [start-fw stop-fw cljs]]<% endif %>
            [<<project-ns>>.core :refer [start-app]]))

(defn start []
  (mount/start-without #'<<project-ns>>.core/repl-server))

(defn stop []
  (mount/stop-except #'<<project-ns>>.core/repl-server))

(defn restart []
  (stop)
  (start))
<% if relational-db %>
(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))
<% endif %>

