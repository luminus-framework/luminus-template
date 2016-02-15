(ns <<project-ns>>.app
  (:require [<<project-ns>>.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)