(ns <<project-ns>>.core
  (:require [<<project-ns>>.handler :as handler]
            [luminus.repl-server :as repl]
            [luminus.http-server :as http]<% if relational-db %>
            [luminus-migrations.core :as migrations]<% endif %>
            [<<project-ns>>.config :refer [env]]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.logging :as log]<% if not war %>
            [<<project-ns>>.env :refer [defaults]]
            [luminus.logger :as logger]<% endif %>
            [mount.core :as mount])
  (:gen-class))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(mount/defstate http-server
                :start
                (http/start
                  (-> env
                      ;;system path, servers such asImmutant uses :path key to indicate server path
                      (dissoc :path)
                      (assoc :handler handler/app)
                      (update :port #(or (-> env :options :port) %))))
                :stop
                (http/stop http-server))

(mount/defstate repl-server
                :start
                (when-let [nrepl-port (env :nrepl-port)]
                  (repl/start {:port nrepl-port}))
                :stop
                (when repl-server
                  (repl/stop repl-server)))
<% if war %>
(defn init-jndi []
  (System/setProperty "java.naming.factory.initial"
                      "org.apache.naming.java.javaURLContextFactory")
  (System/setProperty "java.naming.factory.url.pkgs"
                      "org.apache.naming"))

(defn start-app [args]
  (init-jndi)
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. handler/destroy)))
<% else %>
(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (logger/init (:log-config env))
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  ((:init defaults))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))
<% endif %>
(defn -main [& args]
  <% if relational-db %>(cond
    (some #{"migrate" "rollback"} args)
    (do
      (mount/start #'<<project-ns>>.config/env)
      (migrations/migrate args (env :database-url))
      (System/exit 0))
    :else
    (start-app args)))
  <% else %>(start-app args))<% endif %>
