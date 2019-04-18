(ns leiningen.new.servlet
  (:require [leiningen.new.common :refer :all]))

(defn servlet-features [[assets options :as state]]
  (if (some #{"+servlet"} (:features options))
    [assets
     (assoc options :servlet true)]
    state))
