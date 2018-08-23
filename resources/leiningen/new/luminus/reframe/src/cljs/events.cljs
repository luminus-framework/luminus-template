(ns <<project-ns>>.events
  (:require [re-frame.core :as rf]))

;;dispatchers
<% if reitit %>
(rf/reg-event-db
  :navigate
  (fn [db [_ route]]
    (assoc db :route route)))
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
    {:http {:url "/docs"
            :method :get
            :success-event [:set-docs]}}))

;;subscriptions
<% if reitit %>
(rf/reg-sub
  :route
  (fn [db _]
    (-> db :route)))

(rf/reg-sub
  :page
  :<- [:route]
  (fn [route _]
    (-> route :data :name)))
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