(ns leiningen.new.reitit
  (:require [leiningen.new.common :refer :all]))

(def reitit-assets
  [["{{backend-path}}/{{sanitized}}/routes/home.clj" "reitit/src/home.clj"]])

(defn reitit-features [[assets options :as state]]
  (if (some #{"+reitit"} (:features options))
    [(into assets reitit-assets)
     (-> options
         (append-options :dependencies [['metosin/reitit "0.1.3"]])
         (assoc :reitit true))]
    state))
