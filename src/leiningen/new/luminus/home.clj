(ns {{name}}.routes.home
  (:use compojure.core)
  (:require [{{name}}.views.layout :as layout]))

(defn home-page [] 
  (layout/common
    [:h1 "Hello World!"]
    "This site sure could use some content..."))

(defn about-page []
  (layout/common
   "this is the story of {{name}}... work in progress"))

(defroutes home-routes 
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))