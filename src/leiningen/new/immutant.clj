(ns leiningen.new.immutant
  (:require [leiningen.new.common :refer :all]))

(defn immutant-features [[assets options :as state]]
  (if (some #{"+immutant"} (:features options))
    [assets
     (-> options
         (assoc :server "immutant")
         (append-options :dependencies
                         [['org.immutant/web "2.1.1"]
                          ['org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                          #_['org.immutant/web "2.1.1"
                           :exclusions ['ch.qos.logback/logback-classic]]]))]
    state))

