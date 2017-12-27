(ns leiningen.new.cider
  (:require [leiningen.new.common :refer :all]))

(defn cider-features [[assets options :as state]]
  (if (some #{"+cider"} (:features options))
    [assets
     (-> options
         (assoc :cider true)
         (append-options :dependencies [['cider/cider-nrepl "0.15.1"]]))]
    state))
