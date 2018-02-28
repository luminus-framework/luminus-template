(ns leiningen.new.reitit
  (:require [leiningen.new.common :refer :all]))

(defn reitit-features [[assets options :as state]]
  (if (some #{"+reitit"} (:features options))
    [assets
     (-> options
         (append-options :dependencies [['metosin/reitit "0.1.0"]])
         (assoc :reitit true))]
    state))
