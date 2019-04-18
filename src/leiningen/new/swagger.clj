(ns leiningen.new.swagger
  (:require [leiningen.new.common :refer :all]))

(defn swagger-assets [{:keys [features]}]
  (when (some #{"+reitit"} features)
    [["{{backend-path}}/{{sanitized}}/routes/services.clj" "reitit/src/services.clj"]
     ["{{backend-path}}/{{sanitized}}/middleware/exception.clj" "reitit/src/exception.clj"]]))

(defn swagger-features [[assets options :as state]]
  (if (some #{"+swagger"} (:features options))
    [(into assets (swagger-assets options))
     (-> options
         (assoc :swagger true
                :service-required
                (indent require-indent
                        [[(symbol (str (:project-ns options) ".routes.services")) :refer ['service-routes]]])
                :service-routes
                (indent dev-indent ["#'service-routes"])))]
    state))
