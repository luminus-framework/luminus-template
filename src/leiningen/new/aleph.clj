(ns leiningen.new.aleph
  (:require [leiningen.new.common :refer :all]))

(defn aleph-features [[assets options :as state]]
  (if (some #{"+aleph"} (:features options))
    [assets
     (-> options
         (assoc :server "aleph")
         (append-options :dependencies [['luminus-aleph "0.1.0"]]))]
    state))
