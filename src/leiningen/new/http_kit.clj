(ns leiningen.new.http-kit
  (:require [leiningen.new.common :refer :all]))

(defn http-kit-features [[assets options :as state]]
  (if (some #{"+http-kit"} (:features options))
    [(into assets [["src/<<sanitized>>/core.clj" "httpkit/core.clj"]])
     (assoc options
       :http-kit-dependencies (indent dependency-indent [['http-kit "2.1.19"]])
       :main (symbol (str (:project-ns options) ".core")))]
    state))
