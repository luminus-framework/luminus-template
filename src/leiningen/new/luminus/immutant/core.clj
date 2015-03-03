(ns <<project-ns>>.core
  (:require
    [<<name>>.handler :refer [app]]
    [immutant.web :as immutant]
    [taoensso.timbre :as timbre])
  (:gen-class))

(defonce server (atom nil))

(defn start-server [args]
  "Args should be a flat sequence of key/value pairs corresponding to
  options accepted by `immutant.web/run`. Keys may be keywords or
  strings, but the latter should not include the colon prefix. If the
  -dev option is present, `immutant.web/run-dmc` will be used"
  (let [options (remove #{"-dev"} args)]
    (reset! server
      (if (some #{"-dev"} args)
        (immutant/run-dmc app options)
        (immutant/run app options)))))

(defn stop-server []
  (immutant/stop @server))

(defn -main [& args]
  "e.g. lein run -dev port 3000"
  (start-server args)
  (timbre/info "server started on port:" (:port @server)))
