(ns <<project-ns>>.db.migrations
  (:require
    [migratus.core :as migratus]
    [environ.core :refer [env]]))

(defn migrate [args]
  (let [config {:store :database
                :db {:connection-uri (:database-url env)}}]
    (case (first args)
      "migrate"
      (if (> (count args) 1)
        (apply migratus/up config (rest args))
        (migratus/migrate config))
      "rollback"
      (if (> (count args) 1)
        (apply migratus/rollback config (rest args))
        (migratus/rollback config)))))