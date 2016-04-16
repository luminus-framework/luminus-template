(ns <<project-ns>>.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [<<project-ns>>.layout :refer [error-page]]
            [<<project-ns>>.routes.home :refer [home-routes]]<% if service-required %>
            <<service-required>><% endif %>
            [compojure.route :as route]
            [<<project-ns>>.middleware :as middleware]<% if war %>
            [clojure.tools.logging :as log]
            [<<project-ns>>.config :refer [env]]
            [<<project-ns>>.env :refer [defaults]]
            [mount.core :as mount]
            [luminus.logger :as logger]<% endif %>))
<% if war %>
(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (doseq [component (:started (mount/start))]
    (log/info component "started"))
  ((:init defaults)))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents)
  (log/info "<<name>> has shutdown!"))
<% endif %>
(def app-routes
  (routes<% if service-routes %>
    <<service-routes>><% endif %>
    (wrap-routes #'home-routes middleware/wrap-csrf)
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))

(defn app [] (middleware/wrap-base #'app-routes))
