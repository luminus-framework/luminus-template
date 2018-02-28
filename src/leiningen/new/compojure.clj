(ns leiningen.new.compojure
  (:require [leiningen.new.common :refer :all]))

(defn compojure-features [[assets options :as state]]
  (if (some #{"+compojure"} (:features options))
    [assets
     (-> options
         (append-options :dependencies [['compojure "1.6.1"]])
         (assoc :compojure true))]
    state))
