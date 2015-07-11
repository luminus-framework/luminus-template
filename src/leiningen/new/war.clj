(ns leiningen.new.war
  (:require [leiningen.new.common :refer :all]))

(def war-dependencies
  [['ring-server "0.4.0"]])

(defn ring-options [{:keys [name project-ns]}]
  {:handler      (symbol (str project-ns ".handler/app"))
   :init         (symbol (str project-ns ".handler/init"))
   :destroy      (symbol (str project-ns ".handler/destroy"))
   :uberwar-name (str name ".war")})

(defn war-features [[assets options :as state]]
  (if (some #{"+war"} (:features options))
    [assets
     (-> options
         (assoc :ring-options (indent root-indent (ring-options options)))
         (append-options :plugins [['lein-ring "0.9.6"]]))]
    state))
