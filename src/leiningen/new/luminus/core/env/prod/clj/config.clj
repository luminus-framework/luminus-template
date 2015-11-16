(ns <<project-ns>>.config
  (:require [taoensso.timbre :as timbre]))

(def defaults
  {:init
   (fn []
     (timbre/info "\n-=[myapp started successfully]=-"))
   :middleware identity})
