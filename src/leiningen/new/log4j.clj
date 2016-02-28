(ns leiningen.new.log4j
  (:require [leiningen.new.common :refer :all]))

(def log4j-assets
  [["env/dev/resources/log4j.properties" "core/env/dev/resources/log4j.properties"]
   ["env/prod/resources/log4j.properties" "core/env/prod/resources/log4j.properties"]])

(def log4j-dependencies
  [['luminus-log4j "0.1.3"]])

(defn log4j-features [[assets options :as state]]
  (if (some #{"+log4j"} (:features options))
    [(into assets log4j-assets)
     (append-options options :dependencies log4j-dependencies)]
    state))

