(ns <<project-ns>>.core
  (:require
    [<<name>>.handler :refer [app]]
    [immutant.web :as immutant]
    [environ.core :refer [env]]
    [taoensso.timbre :as timbre])
  (:gen-class))

(defonce server (atom nil))

(defn start-server [args]
  "Args should be a flat sequence of key/value pairs corresponding to
  options accepted by `immutant.web/run`. Keys may be keywords or
  strings, but the latter should not include the colon prefix. If the
  :dev key is present in the environment, `immutant.web/run-dmc` will be used"
  (reset! server
          (if (env :dev)
            (immutant/run-dmc app args)
            (immutant/run app args))))

(defn stop-server []
  (immutant/stop @server))

(defn -main [& args]
  "e.g. lein run -dev port 3000"
  (start-server args)
  (timbre/info "server started on port:" (:port @server)))
