(ns <<project-ns>>.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    <% if reitit %>
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    <% endif %>))

;;dispatchers
<% if reitit %>
(rf/reg-event-db
  :navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :route new-match))))

(rf/reg-fx
  :navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :navigate!
  (fn [_ [_ url-key params query]]
    {:navigate-fx! [url-key params query]}))
<% else %>
(rf/reg-event-db
  :navigate
  (fn [db [_ page]]
    (assoc db :page page)))
<% endif %>
(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success       [:set-docs]}}))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

<% if reitit %>
(rf/reg-event-fx
  :page/init-home
  (fn [_ _]
    {:dispatch [:fetch-docs]}))
<% endif %>

;;subscriptions
<% if reitit %>
(rf/reg-sub
  :route
  (fn [db _]
    (-> db :route)))

(rf/reg-sub
  :page-id
  :<- [:route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :page
  :<- [:route]
  (fn [route _]
    (-> route :data :view)))
<% else %>
(rf/reg-sub
  :page
  (fn [db _]
    (:page db)))
<% endif %>
(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))
