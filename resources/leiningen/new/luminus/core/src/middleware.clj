(ns <<project-ns>>.middleware
  (:require
    [<<project-ns>>.env :refer [defaults]]<% if not service %>
    [cheshire.generate :as cheshire]
    [cognitect.transit :as transit]
    [clojure.tools.logging :as log]
    [<<project-ns>>.layout :refer [error-page]]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
    [<<project-ns>>.middleware.formats :as formats]
    [muuntaja.middleware :refer [wrap-format wrap-params]]<% endif %>
    [<<project-ns>>.config :refer [env]]<% if immutant-session %>
    [ring.middleware.flash :refer [wrap-flash]]
    [immutant.web.middleware :refer [wrap-session]]<% else %>
    [ring-ttl-session.core :refer [ttl-memory-store]]<% endif %>
    [ring.middleware.defaults :refer [site-defaults wrap-defaults]]<% if auth-middleware-required %>
    <<auth-middleware-required>><% if auth-session %>
    <<auth-session>><% endif %><% if auth-jwe %>
    <<auth-jwe>><% endif %><% endif %>)<% if not service %>
  <% if servlet %>(:import [javax.servlet ServletContext])<% endif %>
           <% endif %>)
<% if not service %><% if servlet %>
(defn wrap-context [handler]
  (fn [request]
    (assoc-in request [:session :app-context]
              (if-let [context (:servlet-context request)]
                ;; If we're not inside a servlet environment
                ;; (for example when using mock requests), then
                ;; .getContextPath might not exist
                (try (.getContextPath ^ServletContext context)
                     (catch IllegalArgumentException _ context))
                ;; if the context is not specified in the request
                ;; we check if one has been specified in the environment
                ;; instead
                (:app-context env)))))
<% endif %>
(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t (.getMessage t))
        (error-page {:status 500
                     :title "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title "Invalid anti-forgery token"})}))


(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))
<% endif %><% if auth-middleware-required %><% if not service %>
(defn on-error [request response]
  (error-page
    {:status 403
     :title (str "Access to " (:uri request) " is not authorized")}))
<% else %>
(defn on-error [request response]
  {:status 403
   :headers {}
   :body (str "Access to " (:uri request) " is not authorized")})
<% endif %>
(defn wrap-restricted [handler]
  (restrict handler {:handler authenticated?
                     :on-error on-error}))<% if auth-jwe %>

(def secret (random-bytes 32))

(def token-backend
  (jwe-backend {:secret secret
                :options {:alg :a256kw
                          :enc :a128gcm}}))

(defn token [username]
  (let [claims {:user (keyword username)
                :exp (let [fmt (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ssZ")]
                       (.format fmt
                                (.getTime
                                 (doto (java.util.Calendar/getInstance)
                                   (.setTime (java.util.Date.))
                                   (.add java.util.Calendar/HOUR_OF_DAY 1)))))}]
    (encrypt claims secret {:alg :a256kw :enc :a128gcm})))<% endif %>

(defn wrap-auth [handler]
  (let [backend <% if auth-jwe %>token-backend<% else %><% if auth-session %>(session-backend)<% endif %><% endif %>]
    (-> handler
        (wrap-authentication backend)
        (wrap-authorization backend))))
<% endif %>
(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)<% if auth-middleware-required %>
      wrap-auth<% endif %><% if immutant-session %>
      wrap-flash
      (wrap-session {:cookie-attrs {:http-only true}})
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (dissoc :session)))<% else %>
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (assoc-in  [:session :store] (ttl-memory-store (* 60 30)))))<% endif %><% if not service %><% if servlet %>
      wrap-context<% endif %>
      wrap-internal-error<% endif %>))
