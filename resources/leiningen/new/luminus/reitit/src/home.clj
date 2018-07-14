(ns <<project-ns>>.routes.home
  (:require [<<project-ns>>.layout :as layout]<% if relational-db %>
            [<<project-ns>>.db.core :as db]<% endif %>
            [clojure.java.io :as io]
            [<<project-ns>>.middleware :as middleware]
            [ring.util.http-response :as response]))
<% if cljs  %>
(defn home-page [_]
  (layout/render "home.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]<% if graphql %>
   ["/graphiql" {:get (fn [_] (layout/render "graphiql.html"))}]<% endif %>
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]])
<% else %>
(defn home-page [_]
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [_]
  (layout/render "about.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]<% if graphql %>
   ["/graphiql" {:get (fn [_] (layout/render "graphiql.html"))}]
   <% endif %>
   ["/about" {:get about-page}]])
<% endif %>
