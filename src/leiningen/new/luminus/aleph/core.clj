(ns <<project-ns>>.core
  (:require
    [<<name>>.handler :refer [app init destroy]]
    [aleph.http :as http]
    [ring.middleware.reload :as reload]
    [environ.core :refer [env]]<% if database-profiles %>
    [ragtime.main]<% endif %>
    [taoensso.timbre :as timbre])
  (:gen-class))

(defn parse-port [[port]]
  (Integer/parseInt (or port (env :port) "3000")))

<% if database-profiles %>(defn migrate [args]
                            (ragtime.main/-main
                              "-r" "ragtime.sql.database"
                              "-d" (env :database-url)
                              "-m" "ragtime.sql.files/migrations"
                              (clojure.string/join args)))<% endif %>

(defn start-app [args]
  "e.g. lein run 3000"
  (let [port (parse-port args)]
    (try
      (init)
      (.addShutdownHook (Runtime/getRuntime) (Thread. destroy))
      (http/start-server
        (if (env :dev) (reload/wrap-reload #'app) app)
        {:port port})
      (timbre/info "server started on port:" port)
      (catch Throwable t
        (timbre/error (str "server failed to start on port: " port) t)))))

<% if database-profiles %>(defn migrate [args]
  (ragtime.main/-main
    "-r" "ragtime.sql.database"
    "-d" (env :database-url)
    "-m" "ragtime.sql.files/migrations"
    (clojure.string/join args)))<% endif %>

(defn -main [& args]
  <% if database-profiles %>(case (first args)
    "migrate" (migrate args)
    "rollback" (migrate args)
  (start-app args)))
  <% else %>(start-app args))
<% endif %>
