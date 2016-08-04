(ns <<project-ns>>.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [<<project-ns>>.config :refer [env]]
            [schema.core :as s]))

(defapi service-routes
  (when (:dev env)
    {:swagger {:ui "/swagger-ui"
               :spec "/swagger.json"
               :data {:info {:version "1.0.0"
                             :title "Sample API"
                             :description "Sample Services"}}}})

  (context "/api" []
    :tags ["thingie"]

    (GET "/plus" []
      :return       Long
      :query-params [x :- Long, {y :- Long 1}]
      :summary      "x+y with query-parameters. y defaults to 1."
      (ok (+ x y)))

    (POST "/minus" []
      :return      Long
      :body-params [x :- Long, y :- Long]
      :summary     "x-y with body-parameters."
      (ok (- x y)))

    (GET "/times/:x/:y" []
      :return      Long
      :path-params [x :- Long, y :- Long]
      :summary     "x*y with path-parameters"
      (ok (* x y)))

    (POST "/divide" []
      :return      Double
      :form-params [x :- Long, y :- Long]
      :summary     "x/y with form-parameters"
      (ok (/ x y)))

    (GET "/power" []
      :return      Long
      :header-params [x :- Long, y :- Long]
      :summary     "x^y with header-parameters"
      (ok (long (Math/pow x y))))))
