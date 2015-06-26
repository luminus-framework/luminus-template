(ns <<project-ns>>.core
  (:require [<<project-ns>>.handler :refer [app init destroy]]<% ifequal server "aleph" %>
            [aleph.http :as http]
            <% endifequal %><% ifequal server "http-kit" %>
            [org.httpkit.server :as http-kit]
            <% endifequal %><% ifequal server "immutant" %>
            [immutant.web :as immutant]
            <% endifequal %><% ifequal server "jetty" %>
            [ring.adapter.jetty :refer [run-jetty]]
            <% endifequal %>
            [ring.middleware.reload :as reload]<% if database-profiles %>
            [ragtime.main]<% endif %>
            [taoensso.timbre :as timbre]
            [environ.core :refer [env]])
  (:gen-class))

(defn parse-port [[port]]
  (Integer/parseInt (or port (env :port) "3000")))

<% ifequal server "aleph" %>
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
<% endifequal %>
<% ifequal server "http-kit" %>
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
<% endifequal %>
<% ifequal server "immutant" %>
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
<% endifequal %>
<% ifequal server "jetty" %>
(defonce server (atom nil))

(defn start-server [port]
  (init)
  (reset! server
          (run-jetty
            (if (env :dev) (reload/wrap-reload #'app) app)
            {:port port
             :join? false})))

(defn stop-server []
  (when @server
    (destroy)
    (.stop @server)
    (reset! server nil)))

(defn start-app [args]
  (let [port (parse-port args)]
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server))
    (start-server port)))
<% endifequal %>

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
