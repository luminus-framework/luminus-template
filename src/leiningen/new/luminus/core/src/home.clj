(ns <<project-ns>>.routes.home
  (:require [<<project-ns>>.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))
<% if not cljs  %>
(defn about-page []
  (layout/render "about.html"))
<% endif %>
(defroutes home-routes
  (GET "/" [] (home-page))
  <% if not cljs  %>(GET "/about" [] (about-page))<% endif %>)
