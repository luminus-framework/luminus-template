(ns <<project-ns>>.core
  (:require
    [<<name>>.handler :refer [app init destroy]]
    [ring.middleware.reload :as reload]
    [org.httpkit.server :as http-kit]
    [environ.core :refer [env]]
    [taoensso.timbre :as timbre])
  (:gen-class))

;contains function that can be used to stop http-kit server
(defonce server (atom nil))

(defn parse-port [[port]]
  (if port (Integer/parseInt port) 3000))

(defn start-server [port]
  (init)
  (reset! server
          (http-kit/run-server
            (if (env :dev) (reload/wrap-reload app) app)
            {:port port})))

(defn stop-server []
  (when @server
    (destroy)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& args]
  (let [port (parse-port args)]
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server))
    (start-server port)
    (timbre/info "server started on port:" port)))
