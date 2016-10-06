(ns <<project-ns>>.core)

(defn init! []
  (-> (.getElementById js/document "app")
      (.-innerHTML)
      (set! "Welcome to <<name>>")))
