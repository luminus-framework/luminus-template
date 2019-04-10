(ns <<project-ns>>.app
  (:require [<<project-ns>>.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

<% if kee-frame %>(core/init! false)<%else%>(core/init!)<% endif %>
