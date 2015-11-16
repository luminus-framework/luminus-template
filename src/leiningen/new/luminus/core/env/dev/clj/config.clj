(ns <<project-ns>>.config
  (:require [selmer.parser :as parser]
            [taoensso.timbre :as timbre]
            [ring.middleware.reload :refer [wrap-reload]]
            [selmer.middleware :refer [wrap-error-page]]
            [prone.middleware :refer [wrap-exceptions]]))

(defn wrap-dev [handler]
  (-> handler
      wrap-reload
      wrap-error-page
      wrap-exceptions))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (timbre/info "\n-=[myapp started successfully using the development profile]=-"))
   :middleware wrap-dev})
