(ns <<project-ns>>.core
  (:require [<<project-ns>>.handler :refer [app init destroy]]
            [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]])
  (:gen-class))

(defn parse-port [port]
  (Integer/parseInt (or port (env :port) "3000")))

(defn -main [& [port]]
  (let [port (parse-port port)]
    (init)
    (.addShutdownHook (Runtime/getRuntime) (Thread. destroy))
    (run-jetty app {:port port :join? false})))