(ns {{name}}.middleware
  (:require [taoensso.timbre :as timbre]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [noir.response :refer [content-type]]
            [selmer.middleware :refer [wrap-error-page]]
            [prone.middleware :refer [wrap-exceptions]]
            [noir-exception.core :refer [wrap-internal-error]]))

(defn log-request [handler]
  (fn [req]
    (timbre/debug req)
    (handler req)))

(defn wrap-default-content-type [handler]
  (fn [req]
      (let [response (handler req)]
           (if (and (map? response) (empty? (get-in response [:headers "Content-Type"])))
             (content-type "text/html; charset=utf-8" response)
             response))))

(def development-middleware
  [wrap-error-page
   wrap-exceptions
   wrap-default-content-type])

(def production-middleware
  [#(wrap-internal-error % :log (fn [e] (timbre/error e)))])

(defn load-middleware []
  (concat (when (env :dev) development-middleware)
          production-middleware))
