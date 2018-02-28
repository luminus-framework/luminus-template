<% if reitit %>
(defn home-routes []
  [["/" {:get {:handler    home-page
               :middleware [middleware/wrap-csrf
                            middleware/wrap-formats]}}]
   ["/about" {:get about-page}]])
<% endif %>
