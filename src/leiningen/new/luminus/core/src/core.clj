(ns <<project-ns>>.core
  (:require [<<project-ns>>.handler :refer [app init destroy]]<% ifequal server "aleph" %>
            [aleph.http :as http]<% endifequal %><% ifequal server "http-kit" %>
            [org.httpkit.server :as http-kit]<% endifequal %><% ifequal server "immutant" %>
            [immutant.web :as immutant]<% endifequal %><% ifequal server "jetty" %>
            [qbits.jet.server :refer [run-jetty]]<% endifequal %>
            [ring.middleware.reload :as reload]<% if database-profiles %>
            [<<project-ns>>.db.migrations :as migrations]<% endif %>
            [taoensso.timbre :as timbre]
            [environ.core :refer [env]])
  (:gen-class))
<% ifequal server "aleph" %>
(defn parse-port [[port]]
  (Integer/parseInt (or port (env :port) "3000")))

(defn start-app
  "e.g. lein run 3000"
  [args]
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
<% else %>
(defonce server (atom nil))
<% ifequal server "immutant" %>
(defn start-server
  "Args should be a flat sequence of key/value pairs corresponding to
   options accepted by `immutant.web/run`. Keys may be keywords or
   strings, but the latter should not include the colon prefix. If the
   :dev key is present in the environment, `immutant.web/run-dmc` will be used"
  [args]
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

(defn start-app
  "e.g. lein run -dev port 3000"
  [args]
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server))
  (start-server args)
  (timbre/info "server started on port:" (:port @server)))
<% else %>
(defn parse-port [[port]]
  (Integer/parseInt (or port (env :port) "3000")))
<% ifequal server "http-kit" %>
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
<% endifequal %><% ifequal server "jetty" %>
(defn start-server [port]
  (init)
  (reset! server
          (run-jetty
            {:ring-handler (if (env :dev) (reload/wrap-reload #'app) app)
             :port port
             :join? false})))

(defn stop-server []
  (when @server
    (destroy)
    (.stop @server)
    (reset! server nil)))
<% endifequal %>
(defn start-app [args]
  (let [port (parse-port args)]
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server))
    (timbre/info "server is starting on port " port)
    (start-server port)))
<% endifequal %><% endifequal %>
(defn -main [& args]
  <% if database-profiles %>(cond
    (some #{"migrate" "rollback"} args) (migrations/migrate args)
    :else (start-app args)))
  <% else %>(start-app args))<% endif %>
