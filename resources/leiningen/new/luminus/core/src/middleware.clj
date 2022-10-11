(ns <<project-ns>>.middleware
  (:require
    [<<project-ns>>.env :refer [defaults]]<% if not service %>
    [clojure.tools.logging :as log]
    [<<project-ns>>.layout :refer [error-page]]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
    [<<project-ns>>.middleware.formats :as formats]
    [muuntaja.middleware :refer [wrap-format wrap-params]]<% endif %>
    [<<project-ns>>.config :refer [env]]<% if undertow-based %>
    [ring.middleware.flash :refer [wrap-flash]]<% ifequal server "immutant" %>
    [immutant.web.middleware :refer [wrap-session]]<% else %>
    [ring.adapter.undertow.middleware.session :refer [wrap-session]]<% endifequal %><% else %>
    [ring-ttl-session.core :refer [ttl-memory-store]]<% endif %>
    [ring.middleware.defaults :refer [site-defaults wrap-defaults]]<% if async %>
    [promesa.core :as p]<% endif %><% if auth-middleware-required %>
    <<auth-middleware-required>><% if auth-session %>
    <<auth-session>><% endif %><% if auth-jwe %>
    <<auth-jwe>>[buddy.sign.util :refer [to-timestamp]]<% endif %><% endif %>)
  <% if any auth-jwe servlet %> (:import
    <% if auth-jwe %>[java.util Calendar Date]<% endif %>
    <% if servlet %>[javax.servlet ServletContext]<% endif %>)<% endif %>)
<% if not service %><% if servlet %>
(defn wrap-context [handler]
  (letfn [(update-request [request]
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
                        (:app-context env))))]
    (fn ([request]
         (-> request
             update-request
             handler))
      ([request respond raise]
       (-> request
           update-request
           (handler respond raise))))))
<% endif %>
(defn wrap-internal-error [handler]
  (let [error-result (fn [^Throwable t]
                       (log/error t (.getMessage t))
                       (error-page {:status 500
                                    :title "Something very bad has happened!"
                                    :message "We've dispatched a team of highly trained gnomes to take care of the problem."}))]
    (fn wrap-internal-error-fn
      ([req respond _]
       (handler req respond #(respond (error-result %))))
      ([req]
       (try
         (handler req)
         (catch Throwable t
           (error-result t)))))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title "Invalid anti-forgery token"})}))


(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
    (fn
      ([request]
         ;; disable wrap-formats for websockets
         ;; since they're not compatible with this middleware
       ((if (:websocket? request) handler wrapped) request))
      ([request respond raise]
       ((if (:websocket? request) handler wrapped) request respond raise)))))
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
                :exp (to-timestamp
                       (.getTime
                         (doto (Calendar/getInstance)
                           (.setTime (Date.))
                           (.add Calendar/HOUR_OF_DAY 1))))}]
    (encrypt claims secret {:alg :a256kw :enc :a128gcm})))<% endif %>

(defn wrap-auth [handler]
  (let [backend <% if auth-jwe %>token-backend<% else %><% if auth-session %>(session-backend)<% endif %><% endif %>]
    (-> handler
        (wrap-authentication backend)
        (wrap-authorization backend))))
<% endif %>
(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)<% if auth-middleware-required %>
      wrap-auth<% endif %><% if undertow-based %>
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

<% if async %>
(defn- method-name-and-arity-pred [name n]
  (fn [^java.lang.reflect.Method m]
    (and (= (.getName m) name)
         (== n (-> m .getParameterTypes alength)))))

(defn- function-has-arity? [f n]
  (and (fn? f)
       (let [f2 ^clojure.lang.Fn f
             c (.getClass f2)
             ms (.getDeclaredMethods c)]
         (->> ms
              (filter (method-name-and-arity-pred "invoke" n))
              seq
              ;; support your local garbage collector
              boolean))))


(defn wrap-as-async [handler]
  "Execute a sync-only handler async.
 
 This allows you to work with simple sync handlers.
 Note that this must be the outermost middleware that is executed.

 If the handler is async (= it can take 3 arguments)
 the async version will be called."
  (let [async? (function-has-arity? handler 3)]
    (fn
      ([req]
       ;; sync - server/middleware is calling the shots
       (if async?
         (handler req identity (fn [^Throwable t] (throw t)))
         (let [rsp (handler req)]
           (if (p/promise? rsp)
             (do
               (log/warn "using promise from sync ring handler, must block")
               @rsp)
             rsp))))
      ;; async so far - use sync version if the handler can not talk async
      ([req respond raise]
       (if async?
         (handler req respond raise)
         (-> (p/do (handler req))
             (p/handle (fn [result err]
                         (if result
                           (respond result)
                           (raise err))))))))))

<% endif %>
