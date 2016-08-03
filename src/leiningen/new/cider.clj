(ns leiningen.new.cider
  (:require [leiningen.new.common :refer :all]))

(defn cider-features [[assets options :as state]]
  (if (some #{"+cider"} (:features options))
    [assets
     (-> options
         (assoc :cider true)
         (append-options :dev-plugins [['cider/cider-nrepl "0.14.0-SNAPSHOT"]]))]
    state))
