(ns <<project-ns>>.routes.home
  (:require [<<project-ns>>.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))
<% if  cljs  %>
(defn home-page []
  (layout/render "home.html"))

(defroutes home-routes
  (GET "/" []
       (home-page))<% if  graphql  %>
  (GET "/graphiql" [] (layout/render "graphiql.html"))<% endif %>
  (GET "/docs" []
       (-> (response/ok (-> "docs/docs.md" io/resource slurp))
           (response/header "Content-Type" "text/plain; charset=utf-8"))))
<% else %>
(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
<% endif %>
