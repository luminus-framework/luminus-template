(ns <<project-ns>>.core
  (:require [<<project-ns>>.handler :refer [app init destroy]]
            [luminus.repl-server :as repl]
            [luminus.http-server :as http]<% if relational-db %>
            [<<project-ns>>.db.migrations :as migrations]<% endif %>
            [<<project-ns>>.config :refer [env]]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.logging :as log]
            [mount.core :as mount])
  (:gen-class))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(mount/defstate http-server
                :start
                (http/start
                  {:handler app
                   :init    init
                   :port    (or (-> env :options :port)
                                (:port env))})
                :stop
                (http/stop http-server destroy))

(mount/defstate repl-server
                :start
                (when-let [nrepl-port (env :nrepl-port)]
                  (repl/start {:port nrepl-port}))
                :stop
                (when repl-server
                  (repl/stop repl-server)))

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  <% if relational-db %>(cond
    (some #{"migrate" "rollback"} args)
    (do
      (mount/start #'<<project-ns>>.config/env)
      (migrations/migrate args)
      (System/exit 0))
    :else
    (start-app args)))
  <% else %>(start-app args))<% endif %>
