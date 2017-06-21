(ns <<project-ns>>.env
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [selmer.parser :as parser]
            [<<project-ns>>.dev-middleware :refer [wrap-dev]]))

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

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[<<name>> started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[<<name>> has shut down successfully]=-"))
   :middleware wrap-dev})
