(ns <<project-ns>>.core
  (:require
    [<<name>>.handler :refer [app]]
    [aleph.http :as http]
    [taoensso.timbre :as timbre])
  (:gen-class))

(defn parse-port [[port]]
  (if port (Integer/parseInt port) 3000))

(defn -main [& args]
  "e.g. lein run 3000"
  (let [port (parse-port args)]
    (try
      (http/start-server app {:port port})
      (timbre/info "server started on port:" port)
      (catch Throwable t
        (timbre/error (str "server failed to start on port: " port) t)))))
