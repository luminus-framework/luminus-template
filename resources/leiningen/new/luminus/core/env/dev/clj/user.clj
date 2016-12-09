(ns user
  (:require [mount.core :as mount]<% if figwheel %>
            [<<project-ns>>.figwheel :refer [start-fw stop-fw cljs]]<% endif %>
            <<project-ns>>.core))

(defn start []
  (mount/start-without #'<<project-ns>>.core/http-server
                       #'<<project-ns>>.core/repl-server))

(defn stop []
  (mount/stop-except #'<<project-ns>>.core/http-server
                     #'<<project-ns>>.core/repl-server))

(defn restart []
  (stop)
  (start))


