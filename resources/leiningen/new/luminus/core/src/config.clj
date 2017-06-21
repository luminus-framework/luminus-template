(ns <<project-ns>>.config
  (:require [<<project-ns>>.env :refer [from-profiles]]
            [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [mount.core :refer [args defstate]]))

(defstate env :start (load-config
                       :merge
                       [(args)
                        (source/from-system-props)
                        (source/from-env)
                        (from-profiles)]))
