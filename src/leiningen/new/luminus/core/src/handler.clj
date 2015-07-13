(ns <<project-ns>>.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [ring.middleware.reload :as reload]
            [<<project-ns>>.routes.home :refer [home-routes]]
            <<service-required>>
            [<<project-ns>>.middleware :as middleware]
            [<<project-ns>>.session :as session]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [clojure.tools.nrepl.server :as nrepl]))

(defonce nrepl-server (atom nil))

(defroutes base-routes
           (route/resources "/")
           (route/not-found "Not Found"))

(defn start-nrepl
  "Start a network repl for debugging when the :nrepl-port is set in the environment."
  []
  (when-let [port (env :nrepl-port)]
    (try
      (->> port
           (Integer/parseInt)
           (nrepl/start-server :port)
           (reset! nrepl-server))
      (timbre/info "nREPL server started on port" port)
      (catch Throwable t
        (timbre/error "failed to start nREPL" t)))))

(defn stop-nrepl []
  (when-let [server @nrepl-server]
    (nrepl/stop-server server)))

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

  (if (env :dev) (parser/cache-off!))
  (start-nrepl)
  ;;start the expired session cleanup job
  (session/start-cleanup-job!)
  (timbre/info (str
                 "\n-=[<<name>> started successfully"
                 (when (env :dev) "using the development profile")
                 "]=-")))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "<<name>> is shutting down...")
  (stop-nrepl)
  (timbre/info "shutdown complete!"))

(def app-base
  (-> (routes<% if service-routes %>
        <<service-routes>><% endif %>
        (wrap-routes #'home-routes middleware/wrap-csrf)
        #'base-routes)
      middleware/wrap-base))

(def app
  (if (env :dev) (reload/wrap-reload #'app-base) app-base))
