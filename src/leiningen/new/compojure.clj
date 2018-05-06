(ns leiningen.new.compojure
  (:require [leiningen.new.common :refer :all]))

(def compojure-assets
  [["{{backend-path}}/{{sanitized}}/routes/home.clj" "compojure/src/home.clj"]])

(defn compojure-features [[assets options :as state]]
  (if (some #{"+compojure"} (:features options))
    [(into assets compojure-assets)
     (-> options
         (append-options :dependencies [['compojure "1.6.1"]])
         (assoc :compojure true))]
    state))
