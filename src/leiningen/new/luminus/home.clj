(ns {{name}}.routes.home
  (:use compojure.core hiccup.element)
  (:require [{{name}}.views.layout :as layout]))

(defn home-page [] 
  (layout/common
    [:h1 "Welcome to {{name}}"]
    [:h2 "some links to get started"]
    [:ol 
     [:li (link-to "http://www.luminusweb.net/docs/generating_html.md" "HTML templating")]
     [:li (link-to "http://www.luminusweb.net/docs/database.md" "Accessing the database")]
     [:li (link-to "http://www.luminusweb.net/docs/static_resources.md" "Serving static resources")]
     [:li (link-to "http://www.luminusweb.net/docs/responses.md" "Setting response types")]
     [:li (link-to "http://www.luminusweb.net/docs/routes.md" "Defining routes")]
     [:li (link-to "http://www.luminusweb.net/docs/middleware.md" "Adding middleware")]
     [:li (link-to "http://www.luminusweb.net/docs/sessions_cookies.md" "Sessions and cookies")]
     [:li (link-to "http://www.luminusweb.net/docs/security.md" "Security")]
     [:li (link-to "http://www.luminusweb.net/docs/deployment.md" "Deploying the application")]]))

(defn about-page []
  (layout/common
   "this is the story of {{name}}... work in progress"))

(defroutes home-routes 
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))