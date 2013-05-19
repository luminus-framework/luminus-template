(ns {{name}}.core  
  (:require
    [{{name}}.handler :refer [war-handler]]
    [ring.middleware.reload :as reload]
    [org.httpkit.server :as http-kit]
    [taoensso.timbre :as timbre])
  (:gen-class))

(defn dev? [args] (some #{"-dev"} args))

(defn port [args]
  (if-let [port (first (remove #{"-dev"} args))]
    (Integer/parseInt port)
    8080))

(defn -main [& args]
  (http-kit/run-server
    (if (dev? args) (reload/wrap-reload war-handler) war-handler)
    {:port (port args)})
  (timbre/info "server started on port"))