(ns {{name}}.handler
  (:use {{name}}.routes.home
        compojure.core)
  (:require [noir.util.middleware :as middleware]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [com.postspectacular.rotor :as rotor]))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn destroy []
  (timbre/info "picture-gallery is shutting down"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :error
     :enabled? true
     :async? false ; should be always false for rotor
     :max-message-per-msecs nil
     :fn rotor/append})
  
  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "error.log" :max-size 10000 :backlog 10})
  
  (timbre/info "{{name}} started successfully"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "{{name}} is shutting down..."))

;;append your application routes to the all-routes vector
(def all-routes [home-routes app-routes])

(def app (-> all-routes
             middleware/app-handler
             ;;add your middlewares here
             ))

(def war-handler (middleware/war-handler app))
