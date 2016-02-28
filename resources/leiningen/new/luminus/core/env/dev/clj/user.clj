(ns user
  (:require [mount.core :as mount]
            [<<project-ns>>.config :refer [env]]
            <<project-ns>>.core))

(defn start []
  (mount/start-without #'<<project-ns>>.core/repl-server))

(defn stop []
  (mount/stop-except #'<<project-ns>>.core/repl-server))

(defn restart []
  (stop)
  (start))


