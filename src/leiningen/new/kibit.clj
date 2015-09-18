(ns leiningen.new.kibit
  (:require [leiningen.new.common :refer :all]))

(defn kibit-features [[assets options :as state]]
  (if (some #{"+kibit"} (:features options))
    [assets
     (-> options
         (append-options :plugins [['lein-kibit "0.1.2"]]))]
    state))

