(ns {{name}}.handler
  (:use {{name}}.routes.auth
        {{name}}.routes.home
        compojure.core)
  (:require [noir.util.middleware :as middleware]
            [noir.session :as session]
            [compojure.route :as route]
            [{{name}}.models.db :as db]))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "runs when the application starts and checks if the database
   schema exists, calls db/create-tables if not."
  []
  (if-not (db/initialized?)
    (db/create-tables)))

(defn destroy [] (println "shutting down..."))

;;append your application routes to the all-routes vector
(def all-routes [auth-routes home-routes app-routes])
(def app (middleware/app-handler all-routes))
(def war-handler (middleware/war-handler app))
