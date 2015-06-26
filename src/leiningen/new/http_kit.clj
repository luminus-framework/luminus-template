(ns leiningen.new.http-kit
  (:require [leiningen.new.common :refer :all]))

(defn http-kit-features [[assets options :as state]]
  (if (some #{"+http-kit"} (:features options))
    [assets
     (-> options
         (assoc :server "http-kit")
         (append-options :dependencies [['http-kit "2.1.19"]]))]
    state))
