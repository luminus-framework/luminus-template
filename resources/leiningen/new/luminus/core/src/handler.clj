(ns <<project-ns>>.handler
  (:require [<<project-ns>>.middleware :as middleware]<% if not service %>
            [<<project-ns>>.layout :refer [error-page]]
            [<<project-ns>>.routes.home :refer [home-routes]]<% endif %><% if service-required %>
            <<service-required>><% endif %><% if oauth-required %>
            <<oauth-required>><% endif %><% if compojure %>
            [compojure.core :refer [routes wrap-routes]]<% if compojure %>
            [ring.util.http-response :as response]<% endif %>
            [compojure.route :as route]<% endif %><% if reitit %><% if any service swagger %>
            [reitit.swagger-ui :as swagger-ui]<% endif %>
            [reitit.ring :as ring]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.webjars :refer [wrap-webjars]]<% endif %>
            [<<project-ns>>.env :refer [defaults]]
            [mount.core :as mount]<% if war %>
            [clojure.tools.logging :as log]
            [<<project-ns>>.config :refer [env]]<% endif %>))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))
<% if war %>
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
  (log/info "<<name>> has shut down!"))
<% endif %>
<% include compojure/src/handler-fragment.clj %>
<% include reitit/src/handler-fragment.clj %>
