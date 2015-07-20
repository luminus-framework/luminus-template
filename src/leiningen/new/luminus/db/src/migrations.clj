(ns <<project-ns>>.db.migrations
  (:require
    [migratus.core :as migratus]
    [environ.core :refer [env]]
    [<<project-ns>>.db.core :as db]))

(defn parse-ids [args]
  (map #(Long/parseLong %) (rest args)))

(defn migrate [args]
  (let [config {:store :database
                :db @db/db-spec}]
    (case (first args)
      "migrate"
      (if (> (count args) 1)
        (apply migratus/up config (parse-ids args))
        (migratus/migrate config))
      "rollback"
      (if (> (count args) 1)
        (apply migratus/down config (parse-ids args))
        (migratus/rollback config)))))