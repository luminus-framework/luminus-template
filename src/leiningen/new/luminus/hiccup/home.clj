(ns {{name}}.routes.home
  (:use compojure.core hiccup.element)
  (:require [{{name}}.views.layout :as layout]
            [{{name}}.util :as util]))

(defn home-page []
  (layout/common    
    (util/md->html "/md/docs.md")))

(defn about-page []
  (layout/common    
    "this is the story of {{name}}... work in progress"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))