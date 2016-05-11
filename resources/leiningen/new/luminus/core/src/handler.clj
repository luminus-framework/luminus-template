(ns <<project-ns>>.handler
  (:require [compojure.core :refer [routes wrap-routes]]<% if not service %>
            [<<project-ns>>.layout :refer [error-page]]
            [<<project-ns>>.routes.home :refer [home-routes]]<% endif %><% if service-required %>
            <<service-required>><% endif %>
            [compojure.route :as route]
            [<<project-ns>>.env :refer [defaults]]
            [mount.core :as mount]
            [<<project-ns>>.middleware :as middleware]<% if war %>
            [luminus.logger :as logger]
            [clojure.tools.logging :as log]
            [<<project-ns>>.config :refer [env]]<% endif %>))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))
<% if war %>
(mount/defstate log
                :start (logger/init (:log-config env)))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (doseq [component (:started (mount/start))]
    (log/info component "started")))

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
    <<service-routes>><% endif %><% if not service %>
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))<% endif %>
    (route/not-found<% if service %>
      "page not found"<% else %>
      (:body
        (error-page {:status 404
                     :title "page not found"}))<% endif %>)))


(<% if war %>def app<% else %>defn app []<% endif %> (middleware/wrap-base #'app-routes))
