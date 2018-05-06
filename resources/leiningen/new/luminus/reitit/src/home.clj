(ns <<project-ns>>.routes.home
  (:require [<<project-ns>>.layout :as layout]
            [clojure.java.io :as io]
            [myapp.middleware :as middleware]
            [ring.util.http-response :as response]))
<% if cljs  %>
(defn home-page [_]
  (layout/render "home.html"))

(defn home-routes []
  [["/" {:get {:handler    home-page
               :middleware [middleware/wrap-csrf
                            middleware/wrap-formats]}}]<% if graphql %>
   ["/graphiql" {:get {:handler (fn [_] (layout/render "graphiql.html"))}}]<% endif %>
   ["/docs" {:get {:handler (fn [_]
                              (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                                  (response/header "Content-Type" "text/plain; charset=utf-8")))}}]])
<% else %>
(defn home-page [_]
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [_]
  (layout/render "about.html"))

(defn home-routes []
  [["/" {:get {:handler    home-page
               :middleware [middleware/wrap-csrf
                            middleware/wrap-formats]}}]<% if graphql %>
   ["/graphiql" {:get {:handler (fn [_] (layout/render "graphiql.html"))}}]
   <% endif %>
   ["/about" {:get about-page}]])
<% endif %>
