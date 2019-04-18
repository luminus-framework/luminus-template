(ns <<project-ns>>.routing
  (:require
    [re-frame.core :as rf]))

(def routes
  [["/" :home]
   ["/about" :about]])

(rf/reg-sub
  :nav/route
  :<- [:kee-frame/route]
  identity)

(rf/reg-event-fx
  :nav/route-name
  (fn [_ [_ route-name]]
    {:navigate-to [route-name]}))


(rf/reg-sub
  :nav/page
  :<- [:nav/route]
  (fn [route _]
    (-> route :data :name)))