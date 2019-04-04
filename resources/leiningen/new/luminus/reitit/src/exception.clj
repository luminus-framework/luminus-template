(ns <<project-ns>>.middleware.exception
  (:require [reitit.ring.middleware.exception :as exception]
            [clojure.tools.logging :as log]))

(def exception-middleware
  (exception/create-exception-middleware
   (merge
    exception/default-handlers
    {;; log stack-traces for all exceptions
     ::exception/wrap (fn [handler e request]
                        (log/error e (.getMessage e))
                        (handler e request))})))
