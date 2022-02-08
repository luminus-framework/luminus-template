(ns leiningen.new.logback
  (:require [leiningen.new.common :refer :all]))

(def logback-assets
  [["env/dev/resources/logback.xml" "core/env/dev/resources/logback.xml"]
   ["env/test/resources/logback.xml" "core/env/dev/resources/logback.xml"]
   ["env/prod/resources/logback.xml" "core/env/prod/resources/logback.xml"]])

(def logback-dependencies
  [['ch.qos.logback/logback-classic "1.2.10"]])

(defn logback-features [[assets options :as state]]
  (if (some #{"+logback"} (:features options))
    [(into assets logback-assets)
     (if (some #{"+immutant"} (:features options))
       options
       (append-options options :dependencies logback-dependencies))]
    state))
