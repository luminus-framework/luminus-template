(ns <<project-ns>>.session)

(defonce mem (atom {}))
(def half-hour 1800000)

(defn- current-time []
  (quot (System/currentTimeMillis) 1000))

(defn- expired? [[id session]]
  (pos? (- (:ring.middleware.session-timeout/idle-timeout session) (current-time))))

(defn clear-expired-sessions []
  (clojure.core/swap! mem #(->> % (filter expired?) (into {}))))

(defn start-cleanup-job! []
  (future
    (loop []
      (clear-expired-sessions)
      (Thread/sleep half-hour)
      (recur))))
