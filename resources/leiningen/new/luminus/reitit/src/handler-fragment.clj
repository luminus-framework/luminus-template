(defn- async-aware-default-handler
  ([_] nil)
  ([_ respond _] (respond nil)))

<% if service %>
(mount/defstate app-routes
  :start
  (ring/ring-handler
    (ring/router
      [["/" {:get
             {:handler <% if expanded %>(constantly {:status 301 :headers {"Location" "/api/api-docs/index.html"}}) <% else %>(constantly {:status 200 :body ""})<% endif %>}}]
       (service-routes)<% if oauth %>
       (oauth-routes)<% endif %>])
    (ring/routes
      (ring/create-resource-handler
        {:path "/"})<% if expanded %>
      (wrap-content-type (wrap-webjars async-aware-default-handler))<% endif %>
      (ring/create-default-handler))))
<% else %>
(mount/defstate app-routes
  :start
  (ring/ring-handler
    (ring/router
      [(home-routes)<% if swagger %>
       (service-routes)<% endif %><% if oauth %>
       (oauth-routes)<% endif %>])
    (ring/routes<% if swagger %>
      (swagger-ui/create-swagger-ui-handler
        {:path   "/swagger-ui"
         :url    "/api/swagger.json"
         :config {:validator-url nil}})<% endif %>
      (ring/create-resource-handler
        {:path "/"})<% if expanded %>
      (wrap-content-type
        (wrap-webjars async-aware-default-handler))<% endif %>
      (ring/create-default-handler
        {:not-found
         (constantly (error-page {:status 404, :title "404 - Page not found"}))
         :method-not-allowed
         (constantly (error-page {:status 405, :title "405 - Not allowed"}))
         :not-acceptable
         (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))
<% endif %><% if war %>
(mount/defstate app-handler :start (middleware/wrap-base #'app-routes))

(defn app []
  app-handler)<% else %>
(defn app []
  (middleware/wrap-base #'app-routes))
<% endif %>
