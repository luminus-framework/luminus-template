(ns leiningen.new.aleph
  (:require [leiningen.new.common :refer :all]))

(defn aleph-features [[assets options :as state]]
  (if (some #{"+aleph"} (:features options))
    [(into (remove-conflicting-assets assets "core.clj")
           [["src/<<sanitized>>/core.clj" "aleph/core.clj"]])
     (append-options options :dependencies [['aleph "0.4.0"]])]
    state))
