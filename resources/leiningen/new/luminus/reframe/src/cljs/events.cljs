(ns <<project-ns>>.events
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]))

;;dispatchers
<% if reitit %>
(reg-event-db
  :navigate
  (fn [db [_ route]]
    (assoc db :route route)))
<% else %>
(reg-event-db
  :navigate
  (fn [db [_ page]]
    (assoc db :page page)))
<% endif %>
(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

;;subscriptions
<% if reitit %>
(reg-sub
  :route
  (fn [db _]
    (-> db :route)))

(reg-sub
  :page
  :<- [:route]
  (fn [route _]
    (-> route :data :name)))
<% else %>
(reg-sub
  :page
  (fn [db _]
    (:page db)))
<% endif %>
(reg-sub
  :docs
  (fn [db _]
    (:docs db)))