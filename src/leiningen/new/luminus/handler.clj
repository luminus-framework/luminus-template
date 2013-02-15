(ns {{name}}.handler
  (:use {{name}}.routes.home
        compojure.core)
  (:require [noir.util.middleware :as middleware]
            [compojure.route :as route]))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (println "{{name}} started successfully..."))

(defn destroy []
  (println "shutting down..."))

;;append your application routes to the all-routes vector
(def all-routes [home-routes app-routes])
(def app (-> all-routes
             middleware/app-handler
             ;;add your middlewares here
             ))
(def war-handler (middleware/war-handler app))
