(ns <<project-ns>>.config
  (:require [cprop.core :refer [load-config]]
            [cprop.source :refer [from-resource]]
            [mount.core :refer [args defstate]]))

(defstate env :start (load-config :merge [(args)]))
