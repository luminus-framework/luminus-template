(ns leiningen.new.aleph
  (:require [leiningen.new.common :refer :all]))

(defn aleph-features [[assets options :as state]]
  (if (some #{"+aleph"} (:features options))
    [assets
     (-> options
         (assoc :server "aleph")
         (append-options :http-server-dependencies [['luminus-aleph "0.2.0"]]))]
    state))
