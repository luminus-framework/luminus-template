(ns {{name}}.middleware
  (:require [taoensso.timbre :as timbre]
            [selmer.parser :as parser]
            [environ.core :refer [env]]))

(defn log-request [handler]
  (if (env :dev)
    (fn [req]
      (timbre/debug req)
      (handler req))
    handler))
