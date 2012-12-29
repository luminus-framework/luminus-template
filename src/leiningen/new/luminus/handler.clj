(ns {{name}}.handler
  (:use compojure.core)  
  (:require [noir.util.middleware :as middleware]
            [compojure.route :as route]
            [{{name}}.views.home :as home]))

(defroutes app-routes
  (GET "/" [] (home))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
   app is deployed as a servlet on 
   an app server such as Tomcat

   put any initialization code here"
  []
  (println "{{name}} started successfully..."))


;;append your application routes to the all-routes vector
(def all-routes [app-routes])
(def app (middleware/app-handler all-routes))
(def war-handler (middleware/war-handler app))

