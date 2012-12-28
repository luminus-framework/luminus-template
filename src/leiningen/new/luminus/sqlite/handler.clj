(ns {{name}}.handler
  (:use {{name}}.auth compojure.core)  
  (:require [noir.util.middleware :as middleware]
            [noir.session :as session]
            [compojure.route :as route]
            [{{name}}.common :as common]
            [{{name}}.models.db :as db]))

(defn home [] 
  (common/layout 
    [:h1 
      "Hello " (or (session/get :user) "World!")]))

(defroutes app-routes
  (GET "/" [] (home))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "runs when the application starts and checks if the database
   schema exists, calls db/create-tables if not."
  []
  (if-not (db/initialized?) 
    (db/create-tables)))

;;append your application routes to the all-routes vector
(def all-routes [auth-routes app-routes])
(def app (middleware/app-handler all-routes))
(def war-handler (middleware/war-handler app))
  

