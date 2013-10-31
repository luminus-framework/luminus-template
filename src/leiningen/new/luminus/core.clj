(ns {{name}}.core  
  (:require
    [{{name}}.handler :refer [app]]
    [ring.middleware.reload :as reload]
    [org.httpkit.server :as http-kit]
    [taoensso.timbre :as timbre])
  (:gen-class))

(defn dev? [args] (some #{"-dev"} args))

(defn port [args]
  (if-let [port (first (remove #{"-dev"} args))]
    (Integer/parseInt port)
    3000))

(defn -main [& args]
  (http-kit/run-server
    (if (dev? args) (reload/wrap-reload app) app)
    {:port (port args)})
  (timbre/info "server started on port"))