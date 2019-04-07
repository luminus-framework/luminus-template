(ns leiningen.new.immutant
  (:require [leiningen.new.common :refer :all]))

(def immutant-plugins
  [['lein-immutant "2.1.0"]])

(defn immutant-features [[assets options :as state]]
  (if (some #{"+immutant"} (:features options))
    [assets
     (-> options
         (assoc
           :immutant-session true
           :server "immutant")
         (append-options :plugins immutant-plugins)
         (update :dependencies #(cond-> (remove #{['luminus/ring-ttl-session "0.3.1"]} %)
                                        (some #{"+war"} (:features options))
                                        (conj ['org.webjars/webjars-locator-jboss-vfs "0.1.0"])))
         (append-options :http-server-dependencies [['luminus-immutant "0.2.5"]]))]
    state))
