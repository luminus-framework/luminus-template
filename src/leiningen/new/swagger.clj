(ns leiningen.new.swagger
  (:require [leiningen.new.common :refer :all]))

(def swagger-assets
  [["src/{{sanitized}}/routes/services.clj" "swagger/src/services.clj"]])

(def swagger-dependencies
  [['metosin/compojure-api "0.24.4"]
   ['metosin/ring-swagger-ui "2.1.4-0"]])

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
                (indent dev-indent ['#'service-routes])))]
    state))
