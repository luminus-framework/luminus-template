(ns <<project-ns>>.middleware.exception
  (:require [clojure.tools.logging :as log]
            [expound.alpha :as expound]
            [reitit.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]))

(defn coercion-error-handler [status]
  (let [printer (expound/custom-printer {:print-specs? false})]
    (fn [exception request]
      {:status status
       :headers {"Content-Type" "text/html"}
       :body (with-out-str (printer (-> exception ex-data :problems)))})))

(def exception-middleware
  (exception/create-exception-middleware
   (merge
    exception/default-handlers
    {;; log stack-traces for all exceptions
     ::exception/wrap (fn [handler e request]
                        (log/error e (.getMessage e))
                        (handler e request))
     ;; human-optimized validation message
     ::coercion/request-coercion (coercion-error-handler 400)
     ::coercion/response-coercion (coercion-error-handler 500)})))
