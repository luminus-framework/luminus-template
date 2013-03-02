(ns {{name}}.handler
  (:use {{name}}.routes.auth
        {{name}}.routes.home
        compojure.core)
  (:require [noir.util.middleware :as middleware]
            [noir.session :as session]
            [compojure.route :as route]
            [{{name}}.models.schema :as schema]))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "runs when the application starts and checks if the database
   schema exists, calls schema/create-tables if not."
  []
  (if-not (schema/initialized?)
    (schema/create-tables)))

(defn destroy [] (println "shutting down..."))

;;append your application routes to the all-routes vector
(def all-routes [auth-routes home-routes app-routes])
(def app (middleware/app-handler all-routes))
(def war-handler (middleware/war-handler app))
