(ns {{name}}.session-manager
  (:require [noir.session :refer [mem]]
            [cronj.core :refer [cronj]]))

(defn- current-time []
  (quot (System/currentTimeMillis) 1000))

(defn- expired? [[id session]]
  (pos? (- (:ring.middleware.session-timeout/idle-timeout session) (current-time))))
  
(defn- clear-expired [_ _]  
  (swap! mem #(->> % (filter expired?) (into {}))))

(def cleanup-job
  (cronj
   :entries
   [{:id "session-cleanup"
     :handler clear-expired
     :schedule "* /30 * * * * *"
     :opts {}}]))
