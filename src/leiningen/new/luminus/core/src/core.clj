(ns <<project-ns>>.core
  (:require [<<project-ns>>.handler :refer [app init destroy]]<% ifequal server "aleph" %>
            [aleph.http :as http]<% endifequal %><% ifequal server "http-kit" %>
            [org.httpkit.server :as http-kit]<% endifequal %><% ifequal server "immutant" %>
            [immutant.web :as immutant]<% endifequal %><% ifequal server "jetty" %>
            [qbits.jet.server :refer [run-jetty]]<% endifequal %><% if relational-db %>
            [<<project-ns>>.db.migrations :as migrations]<% endif %>
            [clojure.tools.nrepl.server :as nrepl]
            [taoensso.timbre :as timbre]
            [environ.core :refer [env]])
  (:gen-class))

(defonce nrepl-server (atom nil))

(defn parse-port [port]
  (when port
    (cond
      (string? port) (Integer/parseInt port)
      (number? port) port
      :else          (throw (Exception. (str "invalid port value: " port))))))

(defn stop-nrepl []
  (when-let [server @nrepl-server]
    (nrepl/stop-server server)))

(defn start-nrepl
  "Start a network repl for debugging when the :nrepl-port is set in the environment."
  []
  (if @nrepl-server
    (timbre/error "nREPL is already running!")
    (when-let [port (env :nrepl-port)]
      (try
        (->> port
             (parse-port)
             (nrepl/start-server :port)
             (reset! nrepl-server))
        (timbre/info "nREPL server started on port" port)
        (catch Throwable t
          (timbre/error "failed to start nREPL" t))))))

(defn http-port [port]
  (parse-port (or port (env :port) 3000)))<% ifequal server "aleph" %>

(defn stop-app []
  (stop-nrepl)
  (destroy))

(defn start-app
  "e.g. lein run 3000"
  [[port]]
  (let [port (http-port port)]
    (try
      (init)
      (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app))
      (start-nrepl)
      (http/start-server
        app
        {:port port})
      (timbre/info "server started on port:" port)
      (catch Throwable t
        (timbre/error (str "server failed to start on port: " port) t)))))<% else %>

(defonce server (atom nil))<% ifequal server "immutant" %>

(defn start-http-server [port]
  (init)
  (reset! server (immutant/run app :port port)))

(defn stop-http-server []
  (when @server
    (destroy)
    (immutant/stop @server)
    (reset! server nil)))

(defn stop-app []
  (stop-nrepl)
  (stop-http-server))

(defn start-app [[port]]
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app))
  (start-nrepl)
  (start-http-server (http-port port))
  (timbre/info "server started on port:" (:port @server)))<% else %><% ifequal server "http-kit" %>

(defn start-http-server [port]
  (init)
  (reset! server
          (http-kit/run-server
            app
            {:port port})))

(defn stop-http-server []
  (when @server
    (destroy)
    (@server :timeout 100)
    (reset! server nil)))<% endifequal %><% ifequal server "jetty" %>

(defn start-http-server [port]
  (init)
  (reset! server
          (run-jetty
            {:ring-handler app
             :port port
             :join? false})))

(defn stop-http-server []
  (when @server
    (destroy)
    (.stop @server)
    (reset! server nil)))<% endifequal %>

(defn stop-app []
  (stop-nrepl)
  (stop-http-server))

(defn start-app [[port]]
  (let [port (http-port port)]
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app))
    (start-nrepl)
    (timbre/info "server is starting on port " port)
    (start-http-server port)))<% endifequal %><% endifequal %>

(defn -main [& args]
  <% if relational-db %>(cond
    (some #{"migrate" "rollback"} args) (migrations/migrate args)
    :else (start-app args)))
  <% else %>(start-app args))<% endif %>
