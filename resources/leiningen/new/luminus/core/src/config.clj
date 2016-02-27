(ns <<project-ns>>.config
  (:require [cprop.core :refer [load-config]]
            [cprop.source :refer [from-file]]
            [mount.core :refer [defstate]]))

(defstate env :start (merge
                       (load-config (from-file "config.edn"))
                       (load-config)))
