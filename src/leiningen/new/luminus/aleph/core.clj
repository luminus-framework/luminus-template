(ns <<project-ns>>.core
  (:require
    [<<name>>.handler :refer [app init destroy]]
    [aleph.http :as http]
    [ring.middleware.reload :as reload]
    [environ.core :refer [env]]
    [taoensso.timbre :as timbre])
  (:gen-class))

(defn parse-port [[port]]
  (if port (Integer/parseInt port) 3000))

(defn -main [& args]
  "e.g. lein run 3000"
  (let [port (parse-port args)]
    (try
      (init)
      (.addShutdownHook (Runtime/getRuntime) (Thread. destroy))
      (http/start-server
        (if (env :dev) (reload/wrap-reload app) app)
        {:port port})
      (timbre/info "server started on port:" port)
      (catch Throwable t
        (timbre/error (str "server failed to start on port: " port) t)))))
