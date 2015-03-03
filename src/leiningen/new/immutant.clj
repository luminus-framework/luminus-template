(ns leiningen.new.immutant
  (:require [leiningen.new.common :refer :all]))

(defn immutant-features [[assets options :as state]]
  (if (some #{"+immutant"} (:features options))
    [(into assets [["src/<<sanitized>>/core.clj" "immutant/core.clj"]])
     (-> options
         (append-options :dependencies [['org.immutant/web "2.0.0-beta2"]])
         (assoc :main (symbol (str (:project-ns options) ".core"))))]
    state))
