(ns <<project-ns>>.routes.services
  (:require [muuntaja.middleware :as muuntaja]
            [reitit.ring.coercion :as rrc]
            [reitit.swagger :as swagger]<% if graphql %>
            [<<project-ns>>.routes.services.graphql :as graphql]<% endif %>
            [ring.util.http-response :refer :all]
            [ring.middleware.params :as params]))

(defn service-routes []
  ["/api"
   {:middleware [params/wrap-params
                 muuntaja/wrap-format
                 swagger/swagger-feature
                 rrc/coerce-exceptions-middleware
                 rrc/coerce-request-middleware
                 rrc/coerce-response-middleware]
    :swagger {:id ::api
              :info {:title "my-api"
                     :description "using [reitit](https://github.com/metosin/reitit)."}
              :produces #{"application/json"
                          "application/edn"
                          "application/transit+json"}
              :consumes #{"application/json"
                          "application/edn"
                          "application/transit+json"}}}
   ["/swagger.json"
    {:get {:no-doc true
           :handler (swagger/create-swagger-handler)}}]<% if graphql %>
   ["/graphql" {:post (fn [req] (ok (graphql/execute-request (-> req :body slurp))))}]<% endif %>
   ["/ping" {:get (constantly (ok {:message "ping"}))}]
   ["/pong" {:post (constantly (ok {:message "pong"}))}]])
