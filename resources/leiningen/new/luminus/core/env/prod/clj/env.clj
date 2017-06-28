(ns <<project-ns>>.env
  (:require [clojure.tools.logging :as log]))

(defn from-profiles []
  {})

(def defaults
  {:init
   (fn []
     (log/info "\n-=[<<name>> started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[<<name>> has shut down successfully]=-"))
   :middleware identity})
