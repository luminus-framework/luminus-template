(ns <<project-ns>>.core
  (:require
    [<<name>>.handler :refer [app init destroy]]
    [immutant.web :as immutant]
    [environ.core :refer [env]]<% if database-profiles %>
    [ragtime.main]<% endif %>
    [taoensso.timbre :as timbre])
  (:gen-class))

(defonce server (atom nil))

(defn start-server [args]
  "Args should be a flat sequence of key/value pairs corresponding to
  options accepted by `immutant.web/run`. Keys may be keywords or
  strings, but the latter should not include the colon prefix. If the
  :dev key is present in the environment, `immutant.web/run-dmc` will be used"
  (init)
  (reset! server
          (if (env :dev)
            (immutant/run-dmc #'app args)
            (immutant/run app args))))

(defn stop-server []
  (when @server
    (destroy)
    (immutant/stop @server)
    (reset! server nil)))

(defn start-app [args]
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server))
  (start-server args)
  (timbre/info "server started on port:" (:port @server)))

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