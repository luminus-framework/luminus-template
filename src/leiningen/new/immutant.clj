(ns leiningen.new.immutant
  (:require [leiningen.new.common :refer :all]))

(defn immutant-features [[assets options :as state]]
  (if (some #{"+immutant"} (:features options))
    [(into (remove-conflicting-assets assets "core.clj")
           [["src/<<sanitized>>/core.clj" "immutant/core.clj"]])
     (append-options options :dependencies [['org.immutant/web "2.0.0-beta2"]])]
    state))
