(ns leiningen.new.immutant
  (:require [leiningen.new.common :refer :all]))

(defn immutant-features [[assets options :as state]]
  (if (some #{"+immutant"} (:features options))
    [assets
     (-> options
         (assoc :server "immutant")
         (append-options :dependencies [['org.immutant/web "2.0.2"]]))]
    state))
