(ns leiningen.new.undertow
  (:require [leiningen.new.common :refer :all]))

(defn undertow-features [[assets options :as state]]
  (if (some #{"+undertow"} (:features options))
    [assets
     (-> options
         (assoc
           :undertow-based true
           :server "undertow")
         (update :dependencies conj ['org.webjars/webjars-locator-jboss-vfs "0.1.0"])
         (append-options :http-server-dependencies [['luminus-undertow "0.1.15"]]))]
    state))
