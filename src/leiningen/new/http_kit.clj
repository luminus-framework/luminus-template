(ns leiningen.new.http-kit
  (:require [leiningen.new.common :refer :all]))

(defn http-kit-features [[assets options :as state]]
  (if (some #{"+http-kit"} (:features options))
    [(into (remove-conflicting-assets assets "core.clj")
           [["src/<<sanitized>>/core.clj" "httpkit/core.clj"]])
     (append-options options :dependencies [['http-kit "2.1.19"]])]
    state))
