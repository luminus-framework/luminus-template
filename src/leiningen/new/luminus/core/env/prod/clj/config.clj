(ns <<project-ns>>.config
  (:require [taoensso.timbre :as timbre]))

(def defaults
  {:init
   (fn []
     (timbre/info "\n-=[<<name>> started successfully]=-"))
   :middleware identity})
