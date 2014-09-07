(ns {{name}}.session-manager
  (:require [noir.session :refer [clear-expired-sessions]]
            [cronj.core :refer [cronj]]))

(def cleanup-job
  (cronj
    :entries
    [{:id "session-cleanup"
      :handler (fn [_ _] (clear-expired-sessions))
      :schedule "* /30 * * * * *"
      :opts {}}]))
