(ns <<project-ns>>.oauth
  (:require [oauth.client :as oauth]
            [config.core :refer [env]]
            [clojure.tools.logging :as log]))

;;Twitter used as an example, replace these URIs with the OAuth provider of your choice

(def request-token-uri
  "https://api.twitter.com/oauth/request_token")

(def access-token-uri
  "https://api.twitter.com/oauth/access_token")

(def authorize-uri
  "https://api.twitter.com/oauth/authenticate")

(def consumer
  (oauth/make-consumer (env :oauth-consumer-key)
                       (env :oauth-consumer-secret)
                       request-token-uri
                       access-token-uri
                       authorize-uri
                       :hmac-sha1))

(defn oauth-callback-uri
  "Generates the oauth request callback URI"
  [{:keys [headers]}]
  (str (or (headers "x-forwarded-proto") "http") "://" (headers "host") "/oauth/my-oauth-callback"))

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
