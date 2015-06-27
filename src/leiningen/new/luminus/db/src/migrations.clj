(ns <<project-ns>>.db.migrations
  (:require
    [ragtime.jdbc :as jdbc]
    [ragtime.repl :as repl]
    [environ.core :refer [env]]))

(defn migrate [args]
  (let [config {:database   (jdbc/sql-database {:connection-uri (:database-url env)})
                :migrations (jdbc/load-resources "migrations")}]
    (case (first args)
      "migrate"  (repl/migrate config)
      "rollback" (repl/rollback config))))