(ns <<project-ns>>.figwheel
  (:require [leiningen.core.project :as p]
            [figwheel-sidecar.repl-api :as ra]))

(def figwheel-config
  (let [project-config (p/read "project.clj" [:dev])
        figwheel-opts (:figwheel project-config)
        all-builds (get-in project-config [:cljsbuild :builds])]
    {:figwheel-options figwheel-opts
     :all-builds all-builds}))

(defn start-fw []
  (ra/start-figwheel! figwheel-config))

(defn stop-fw []
  (ra/stop-figwheel!))

(defn cljs []
  (ra/cljs-repl))

