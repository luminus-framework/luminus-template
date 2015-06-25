(ns <<project-ns>>.core
  (:require
    [<<name>>.handler :refer [app init destroy]]
    [ring.middleware.reload :as reload]
    [org.httpkit.server :as http-kit]
    [environ.core :refer [env]]<% if database-profiles %>
    [ragtime.main]<% endif %>
    [taoensso.timbre :as timbre])
  (:gen-class))

;contains function that can be used to stop http-kit server
(defonce server (atom nil))

(defn parse-port [[port]]
  (Integer/parseInt (or port (env :port) "3000")))

(defn start-server [port]
  (init)
  (reset! server
          (http-kit/run-server
            (if (env :dev) (reload/wrap-reload #'app) app)
            {:port port})))

(defn stop-server []
  (when @server
    (destroy)
    (@server :timeout 100)
    (reset! server nil)))

(defn start-app [args]
  (let [port (parse-port args)]
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server))
    (start-server port)
    (timbre/info "server started on port:" port)))

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