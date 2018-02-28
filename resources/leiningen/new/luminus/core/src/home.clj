(ns <<project-ns>>.routes.home
  (:require [<<project-ns>>.layout :as layout]<% if relational-db %>
            [<<project-ns>>.db.core :as db]<% endif %>
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))
<% include compojure/home-fragment.clj %>
<% include reitit/home-fragment.clj %>

