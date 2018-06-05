<% if reitit %><% if service %>
(mount/defstate app
  :start
  (ring/ring-handler
    (service-routes)
    (middleware/wrap-base
      (ring/routes
        (swagger-ui/create-swagger-ui-handler {:path "/" :url "/api/swagger.json"})
        (ring/create-resource-handler {:path "/"})
        (wrap-content-type (wrap-webjars (constantly nil)))
        (ring/create-default-handler)))))<% else %>
(mount/defstate app
  :start
  (ring/ring-handler
    (ring/router
      [(home-routes)<% if swagger %>
       (service-routes)<% endif %>])
    (ring/routes<% if swagger %>
      (swagger-ui/create-swagger-ui-handler
        {:path "/swagger-ui"
         :url "/api/swagger.json"
         :config {:validator-url nil}})<% endif %>
      (ring/create-resource-handler
        {:path "/"})
      (wrap-content-type
        (wrap-webjars (constantly nil)))
      (ring/create-default-handler
        {:not-found
         (constantly (error-page {:status 404, :title "404 - Page not found"}))
         :method-not-allowed
         (constantly (error-page {:status 405, :title "405 - Not allowed"}))
         :not-acceptable
         (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))
<% endif %><% endif %>
