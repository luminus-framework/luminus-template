(ns leiningen.new.ctmx
  (:require [leiningen.new.common :refer :all]))

(def ctmx-assets
  [["{{backend-path}}/{{sanitized}}/routes/home.clj" "ctmx/src/home.clj"]])

(defn ctmx-features [[assets options :as state]]
  (if (some #{"+ctmx"} (:features options))
    [(into (remove-conflicting-assets assets "home.clj") ctmx-assets)
     (-> options
         (append-options :dependencies [['ctmx "1.4.3"]])
         (assoc :ctmx true))]
    state))
