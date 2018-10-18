(ns <<project-ns>>.routing
  (:require
    [re-frame.core :as rf]
    [reitit.core :as reitit])
  (:import goog.History))

(def routes
  [["/" :home]
   ["/about" :about]])

(rf/reg-sub
  :nav/route
  :<- [:kee-frame/route]
  identity)

(rf/reg-event-fx
  :nav/by-route-name
  (fn [_ [_ route-name]]
    (let [route (reitit/match-by-name router route-name)]
      {:navigate-to [(-> route :data :name) (:path-params route)]})))


(rf/reg-sub
  :nav/page
  :<- [:nav/route]
  (fn [route _]
    (-> route :data :name)))