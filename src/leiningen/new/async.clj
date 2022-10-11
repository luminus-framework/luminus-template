(ns leiningen.new.async
  (:require [leiningen.new.common :refer :all]))

(def async-dependencies
  [['funcool/promesa "9.0.470"]])

(defn async-features [[assets options :as state]]
  (if (some #{"+async"} (:features options))
    [assets
     (-> options
         (assoc :async true)
         (append-options :dependencies async-dependencies))]
    state))
