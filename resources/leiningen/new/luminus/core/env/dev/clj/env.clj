(ns <<project-ns>>.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [<<project-ns>>.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[<<name>> started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[<<name>> has shut down successfully]=-"))
   :middleware wrap-dev})
