(ns leiningen.new.immutant
  (:require [leiningen.new.common :refer :all]))

(defn immutant-features [[assets options :as state]]
  (if (some #{"+immutant"} (:features options))
    [assets
     (-> options
         (assoc :server "immutant")
         (append-options :dependencies
                         [['org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                          ['luminus-immutant "0.1.0"]]))]
    state))

