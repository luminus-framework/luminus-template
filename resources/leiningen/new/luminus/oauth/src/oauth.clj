(ns <<project-ns>>.oauth
  (:require [<<project-ns>>.config :refer [env]]
            [oauth.client :as oauth]
            [mount.core :refer [defstate]]
            [clojure.tools.logging :as log]))

(defstate consumer
  :start (oauth/make-consumer
           (env :oauth-consumer-key)
           (env :oauth-consumer-secret)
           (env :request-token-uri)
           (env :access-token-uri)
           (env :authorize-uri)
           :hmac-sha1))

(defn oauth-callback-uri
  "Generates the oauth request callback URI"
  [{:keys [headers]}]
  (str (headers "x-forwarded-proto") "://" (headers "host") "/oauth/twitter-callback"))

(defn fetch-request-token
  "Fetches a request token."
  [request]
  (let [callback-uri (oauth-callback-uri request)]
    (log/info "Fetching request token using callback-uri" callback-uri)
    (oauth/request-token consumer (oauth-callback-uri request))))

(defn fetch-access-token
  [request_token]
  (oauth/access-token consumer request_token (:oauth_verifier request_token)))

(defn auth-redirect-uri
  "Gets the URI the user should be redirected to when authenticating."
  [request-token]
  (str (oauth/user-approval-uri consumer request-token)))
