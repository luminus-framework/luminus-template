(ns <<project-ns>>.ajax
  (:require [ajax.core :as ajax]<% if re-frame %>
            [re-frame.core :as rf]<% endif %>))

(defn local-uri? [{:keys [uri]}]
  (not (re-find #"^\w+?://" uri)))

(defn default-headers [request]
  (if (local-uri? request)
    (-> request<% if servlet %>
        (update :uri #(str js/context %))<% endif %>
        (update :headers #(merge {"x-csrf-token" js/csrfToken} %)))
    request))

(defn load-interceptors! []
  (swap! ajax/default-interceptors
         conj
         (ajax/to-interceptor {:name "default headers"
                               :request default-headers})))
<% if re-frame %>
(def http-methods
  {:get    ajax/GET
   :post   ajax/POST
   :put    ajax/PUT
   :delete ajax/DELETE})

(rf/reg-fx
  :http
  (fn [{:keys [method
               url
               success-event
               error-event
               params
               ajax-map]
        :or   {error-event [:common/set-error]
               ajax-map    {}}}]
    ((http-methods method)
      url (merge
            {:params        params
             :handler       (fn [response]
                              (when success-event
                                (rf/dispatch (conj success-event response))))
             :error-handler (fn [error]
                              (rf/dispatch (conj error-event error)))}
            ajax-map))))
<% endif %>
<% if kee-frame %>
(def ajax-chain
  {;; Is the effect in the map?
   :effect-present? (fn [effects] (:http effects))
   ;; The dispatch set for this effect in the map returned from the event handler
   :get-dispatch    (fn [effects]
                      (get-in effects [:http :success-event]))
   ;; Framework will call this function to insert inferred dispatch to next handler in chain
   :set-dispatch    (fn [effects dispatch]
                      (assoc-in effects [:http :success-event] dispatch))})
<% endif %>

