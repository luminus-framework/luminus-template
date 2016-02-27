(ns <<project-ns>>.config
  (:require [cprop.core :refer [load-config]]
            [cprop.source :refer [from-resource]]
            [mount.core :refer [args defstate]]))


(defstate env :start (merge
                       (load-config (from-resource "config.edn"))
                       (load-config)
                       (args)))
