(ns {{name}}.middleware
  (:require [taoensso.timbre :as timbre]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [selmer.middleware :refer [wrap-error-page]]
            [noir-exception.core
              :refer [wrap-internal-error wrap-exceptions]]))

(defn log-request [handler]
  (fn [req]
    (timbre/debug req)
    (handler req)))

(defn load-middleware []
  (into
    (if-let [dev (env :dev)]
      [log-request
       #(wrap-error-page % dev)
       wrap-exceptions])
    [#(wrap-internal-error % :log (fn [e] (timbre/error e)))]))
