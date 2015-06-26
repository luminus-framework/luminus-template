(ns leiningen.new.aleph
  (:require [leiningen.new.common :refer :all]))

(defn aleph-features [[assets options :as state]]
  (if (some #{"+aleph"} (:features options))
    [assets
     (-> options
         (assoc :server "aleph")
         (append-options :dependencies [['aleph "0.4.0"]]))]
    state))
