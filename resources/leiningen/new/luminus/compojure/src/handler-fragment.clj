<% if compojure %>
(mount/defstate app
  :start
  (middleware/wrap-base
    (routes<% if not service %>
      (-> #'home-routes
          (wrap-routes middleware/wrap-csrf)
          (wrap-routes middleware/wrap-formats))<% endif %><% if oauth-routes %>
      <<oauth-routes>><% endif %><% if service-routes %>
      <<service-routes>><% endif %>
      (route/not-found<% if service %>
        "page not found"<% else %>
        (:body
          (error-page {:status 404
                       :title "page not found"}))<% endif %>))))
<% endif %>
