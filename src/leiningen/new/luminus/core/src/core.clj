(ns <<project-ns>>.core
  (:require [<<project-ns>>.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn parse-port [port]
  (Integer/parseInt (or port (System/getenv "PORT") "3000")))

(defn -main [& [port]]
  (let [port (parse-port port)]
    (run-jetty app {:port port :join? false})))