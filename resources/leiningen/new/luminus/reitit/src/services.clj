(ns <<project-ns>>.routes.services
  (:require [muuntaja.middleware :as muuntaja]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as rrc]
            [reitit.swagger :as swagger]
            [ring.middleware.params :as params]))

(defn service-routes []
  (ring/router
    [["/api"
      {:swagger {:id ::default}}
      ["/swagger.json"
       {:get {:no-doc true
              :swagger {:info {:title "my-api"}}
              :handler (swagger/create-swagger-handler)}}]
      ["/ping" {:get (constantly "ping")}]
      ["/pong" {:post (constantly "pong")}]]]
    {:data {:middleware [params/wrap-params
                         muuntaja/wrap-format
                         swagger/swagger-feature
                         rrc/coerce-exceptions-middleware
                         rrc/coerce-request-middleware
                         rrc/coerce-response-middleware]
            :swagger {:produces #{"application/json"
                                  "application/edn"
                                  "application/transit+json"}
                      :consumes #{"application/json"
                                  "application/edn"
                                  "application/transit+json"}}}}))
