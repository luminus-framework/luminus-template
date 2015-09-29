(ns leiningen.new.war
  (:require [leiningen.new.common :refer :all]))

(defn ring-options [{:keys [name project-ns]}]
  {:handler      (symbol (str project-ns ".handler/app"))
   :init         (symbol (str project-ns ".handler/init"))
   :destroy      (symbol (str project-ns ".handler/destroy"))
   :name (str name ".war")})

(defn war-features [[assets options :as state]]
  (if (some #{"+war"} (:features options))
    [assets
     (-> options
         (assoc :uberwar-options (indent root-indent (ring-options options)))
         (append-options :plugins [['lein-uberwar "0.1.0"]]))]
    state))
