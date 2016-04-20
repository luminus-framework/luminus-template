(ns leiningen.new.immutant
  (:require [leiningen.new.common :refer :all]))

(defn immutant-features [[assets options :as state]]
  (if (some #{"+immutant"} (:features options))
    [assets
     (-> options
         (assoc
           :immutant-session true
           :server "immutant")
         (append-options :http-server-dependencies
                         [['org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                          ['luminus-immutant "0.2.0"]]))]
    state))

