(ns <<project-ns>>.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(s/defschema Thingie {:id Long
                      :hot Boolean
                      :tag (s/enum :kikka :kukka)
                      :chief [{:name String
                               :type #{{:id String}}}]})

(defapi service-routes
  (ring.swagger.ui/swagger-ui
   "/swagger-ui")
  (swagger-docs
   :title "Sample api")
  (swaggered "thingie"
   :description "There be thingies"
   (context "/api" []

    (GET* "/plus" []
          :return       Long
          :query-params [x :- Long {y :- Long 1}]
          :summary      "x+y with query-parameters. y defaults to 1."
          (ok (+ x y)))

    (POST* "/minus" []
           :return      Long
           :body-params [x :- Long y :- Long]
           :summary     "x-y with body-parameters."
           (ok (- x y)))

    (GET* "/times/:x/:y" []
          :return      Long
          :path-params [x :- Long y :- Long]
          :summary     "x*y with path-parameters"
          (ok (* x y)))

    (POST* "/divide" []
           :return      Double
           :form-params [x :- Long y :- Long]
           :summary     "x/y with form-parameters"
           (ok (/ x y)))

    (GET* "/power" []
          :return      Long
          :header-params [x :- Long y :- Long]
          :summary     "x^y with header-parameters"
          (ok (long (Math/pow x y))))

    (PUT* "/echo" []
          :return   [{:hot Boolean}]
          :body     [body [{:hot Boolean}]]
          :summary  "echoes a vector of anonymous hotties"
          (ok body))

    (POST* "/echo" []
           :return   Thingie
           :body     [thingie Thingie]
           :summary  "echoes a Thingie from json-body"
           (ok thingie)))))
