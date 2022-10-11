(ns leiningen.new.war
  (:require [leiningen.new.common :refer :all]
            [clojure.set :refer [rename-keys]]))

(defn ring-options [{:keys [name project-ns] :as options}]
  (let [opts {:handler      (symbol (str project-ns ".handler/app-handler"))
              :init         (symbol (str project-ns ".handler/init"))
              :destroy      (symbol (str project-ns ".handler/destroy"))
              :name (str name ".war")}]
    ;; put it in war, put it in async, put it in a third place?
    (if (some #{"+async"} (:features options))
      (assoc opts
             :servlet-version "3.1"
             :async?       true)
      opts)))

(defn update-assets [assets]
  (map
   #(if (and (vector? %) (= (second %) "core/src/core.clj"))
      (assoc % 0 "env/dev/clj/{{sanitized}}/core.clj") %)
   assets))

(defn war-features [[assets options :as state]]
  (if (some #{"+war"} (:features options))
    [(update-assets assets)
     (-> options
         (dissoc :immutant-session :http-server-dependencies)
         (assoc
          :war true
          :uberwar-options (indent root-indent (ring-options options)))
         (update :dependencies (fn [dependencies]
                                 (if (some #{['luminus/ring-ttl-session "0.3.3"]} dependencies)
                                   dependencies
                                   (conj dependencies ['luminus/ring-ttl-session "0.3.3"]))))
         (append-options :dependencies [['ring/ring-servlet "1.9.6"]])
         (append-options :dev-dependencies (into [['directory-naming/naming-java "0.8"]]
                                                 (:http-server-dependencies options)))
         (append-options :plugins (if (some #{"+lein"} (:features options))
                                    [['lein-uberwar "0.2.3"]])))]
    state))
