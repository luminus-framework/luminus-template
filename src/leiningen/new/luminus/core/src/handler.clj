(ns <<project-ns>>.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [<<project-ns>>.layout :refer [error-page]]
            [<<project-ns>>.routes.home :refer [home-routes]]<% if service-required %>
            <<service-required>><% endif %>
            [<<project-ns>>.middleware :as middleware]<% if relational-db %><% ifunequal db-type "h2" %>
            [<<project-ns>>.db.core :as db]<% endifunequal %><% endif %>
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []

  (timbre/merge-config!
    {:level     (if (env :dev) :trace :info)
     :appenders {:rotor (rotor/rotor-appender
                          {:path "<<sanitized>>.log"
                           :max-size (* 512 1024)
                           :backlog 10})}})

  (if (env :dev) (parser/cache-off!))<% if relational-db %><% ifunequal db-type "h2" %>
  (db/connect!)<% endifunequal %><% endif %>
  (timbre/info (str
                 "\n-=[<<name>> started successfully"
                 (when (env :dev) " using the development profile")
                 "]=-")))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "<<name>> is shutting down...")<% if relational-db %><% ifunequal db-type "h2" %>
  (db/disconnect!)<% endifunequal %><% endif %>
  (timbre/info "shutdown complete!"))

(def app-routes
  (routes<% if service-routes %>
    <<service-routes>><% endif %>
    (wrap-routes #'home-routes middleware/wrap-csrf)
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))

(def app (middleware/wrap-base #'app-routes))
