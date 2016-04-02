(ns <<project-ns>>.figwheel
  (:require [figwheel-sidecar.repl-api :as ra]))

(defn project->map []
  (->> (slurp "project.clj")
       (read-string)
       (drop 3)
       (partition 2)
       (map vec)
       (into {})))

(defn deep-merge [a b]
  (merge-with
    (fn [x y]
      (cond (map? y) (deep-merge x y)
            (vector? y) (concat x y)
            :else y))
    a b))

(def figwheel-config
  (let [project-opts (project->map)]
    {:figwheel-options
     (get-in project-opts [:profiles :project/dev :figwheel])
     :all-builds
     (deep-merge
       (get-in project-opts [:cljsbuild :builds])
       (get-in project-opts [:profiles :project/dev :cljsbuild :builds]))}))

(defn start-fw []
  (ra/start-figwheel! figwheel-config))

(defn stop-fw []
  (ra/stop-figwheel!))

(defn cljs []
  (ra/cljs-repl))

