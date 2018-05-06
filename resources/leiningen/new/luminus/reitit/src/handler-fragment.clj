<% if reitit %>
(mount/defstate app
  :start
  (ring/ring-handler
    (ring/router
      (home-routes)
      {:data {:middleware [middleware/wrap-base]}})
    (ring/routes
      (ring/create-resource-handler {:path "/"})
      (wrap-content-type (wrap-webjars (constantly nil)))
      (ring/create-default-handler
        {:not-found
         (constantly (error-page {:status 404, :title "404 - Page not found"}))
         :method-not-allowed
         (constantly (error-page {:status 405, :title "405 - Not allowed"}))
         :not-acceptable
         (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))
<% endif %>
