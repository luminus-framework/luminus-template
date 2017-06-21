(ns <<project-ns>>.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [mount.core :refer [args defstate]]))

(defn read-profile [profiles-file profile]
  (when (.exists (io/file profiles-file))
    (-> profiles-file slurp edn/read-string profile :env)))

(defn from-profiles []
  (let [profiles-file "profiles.clj"
        env (-> "config.edn" io/resource slurp edn/read-string :env)]
    (case env
      :dev (read-profile profiles-file :profiles/dev)
      :test (read-profile profiles-file :profiles/test)
      {})))

(defstate env :start (load-config
                       :merge
                       [(args)
                        (source/from-system-props)
                        (source/from-env)
                        (from-profiles)]))
