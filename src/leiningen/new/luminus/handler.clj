(ns {{name}}.handler
  (:use compojure.core)  
  (:require [lib-luminus.middleware :as middleware]
            [compojure.route :as route]
            [{{name}}.common :as common]))

(defn home [] 
  (common/layout [:h1 "Hello World!"]))

(defroutes app-routes
  (GET "/" [] (home))
  (route/resources "/")
  (route/not-found "Not Found"))


;;append your application routes to the all-routes vector
(def all-routes [app-routes])
(def app (middleware/app-handler all-routes))
(def war-handler (middleware/war-handler app))
  

