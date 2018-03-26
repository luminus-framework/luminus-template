(ns leiningen.new.swagger
  (:require [leiningen.new.common :refer :all]))

(def swagger-assets
  [["{{backend-path}}/{{sanitized}}/routes/services.clj" "swagger/src/services.clj"]])

(def swagger-dependencies
  [['metosin/compojure-api "1.1.12"]])

(defn swagger-features [[assets options :as state]]
  (if (some #{"+swagger"} (:features options))
    [(into assets swagger-assets)
     (-> options
         (append-options :dependencies swagger-dependencies)
         (assoc :swagger true
                :service-required
                (indent require-indent
                        [[(symbol (str (:project-ns options) ".routes.services")) :refer ['service-routes]]])
                :service-routes
                (indent dev-indent ["#'service-routes"])))]
    state))
