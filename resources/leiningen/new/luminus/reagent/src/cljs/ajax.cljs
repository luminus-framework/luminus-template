(ns <<project-ns>>.ajax
  (:require
    [ajax.core :as ajax]
    [luminus-transit.time :as time]
    [cognitect.transit :as transit]<% if re-frame %>
    [re-frame.core :as rf]<% endif %>))

(defn local-uri? [{:keys [uri]}]
  (not (re-find #"^\w+?://" uri)))

(defn default-headers [request]
  (if (local-uri? request)
    (-> request<% if servlet %>
        (update :uri #(str js/context %))<% endif %>
        (update :headers #(merge {"x-csrf-token" js/csrfToken} %)))
    request))

;; injects transit serialization config into request options
<% if re-frame %>
(defn as-transit [opts]
  (merge {:format          (ajax/transit-request-format
                             {:writer (transit/writer :json time/time-serialization-handlers)})
          :response-format (ajax/transit-response-format
                             {:reader (transit/reader :json time/time-deserialization-handlers)})}
         opts))
<% else %>
(defn as-transit [opts]
  (merge {:raw             false
          :format          :transit
          :response-format :transit
          :reader          (transit/reader :json time/time-deserialization-handlers)
          :writer          (transit/writer :json time/time-serialization-handlers)}
         opts))
<% endif %>
(defn load-interceptors! []
  (swap! ajax/default-interceptors
         conj
         (ajax/to-interceptor {:name "default headers"
                               :request default-headers})))
