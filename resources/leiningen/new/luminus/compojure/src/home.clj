(ns <<project-ns>>.routes.home
  (:require [<<project-ns>>.layout :as layout]<% if relational-db %>
            [<<project-ns>>.db.core :as db]<% endif %>
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))
<% if cljs  %>
(defn home-page [request]
  (layout/render request "home.html"))

(defroutes home-routes
  (GET "/" request (home-page request))<% if graphql %>
  (GET "/graphiql" request (layout/render request "graphiql.html"))<% endif %>
  (GET "/docs" []
       (-> (response/ok (-> "docs/docs.md" io/resource slurp))
           (response/header "Content-Type" "text/plain; charset=utf-8"))))
<% else %>
(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))

(defroutes home-routes
  (GET "/" request (home-page request))<% if graphql %>
  (GET "/graphiql" request (layout/render request "graphiql.html"))<% endif %>
  (GET "/about" request (about-page request)))
<% endif %>
